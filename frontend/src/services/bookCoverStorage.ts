const LEGACY_STORAGE_KEY = 'library.book.covers';
const COVER_STORAGE_PREFIX = 'library.book.cover.';
const COVER_INDEX_KEY = 'library.book.cover.index';
const DATABASE_NAME = 'library-system-assets';
const DATABASE_VERSION = 1;
const COVER_STORE_NAME = 'book-covers';
const MAX_COVER_WIDTH = 360;
const MAX_COVER_HEIGHT = 540;
const COVER_QUALITY = 0.72;

interface CoverRecord {
  bookId: number;
  dataUrl: string;
  updatedAt: number;
}

let databasePromise: Promise<IDBDatabase> | null = null;
let migrationPromise: Promise<void> | null = null;

function isBrowser() {
  return typeof window !== 'undefined';
}

function requestToPromise<T>(request: IDBRequest<T>) {
  return new Promise<T>((resolve, reject) => {
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error || new Error('IndexedDB request failed.'));
  });
}

function transactionToPromise(transaction: IDBTransaction) {
  return new Promise<void>((resolve, reject) => {
    transaction.oncomplete = () => resolve();
    transaction.onerror = () => reject(transaction.error || new Error('IndexedDB transaction failed.'));
    transaction.onabort = () => reject(transaction.error || new Error('IndexedDB transaction aborted.'));
  });
}

function openDatabase() {
  if (!databasePromise) {
    databasePromise = new Promise((resolve, reject) => {
      if (!isBrowser() || !window.indexedDB) {
        reject(new Error('IndexedDB is unavailable in this browser.'));
        return;
      }

      const request = window.indexedDB.open(DATABASE_NAME, DATABASE_VERSION);

      request.onupgradeneeded = () => {
        const database = request.result;

        if (!database.objectStoreNames.contains(COVER_STORE_NAME)) {
          database.createObjectStore(COVER_STORE_NAME, { keyPath: 'bookId' });
        }
      };

      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error || new Error('Unable to open cover storage.'));
    });
  }

  return databasePromise;
}

function readLegacyCoverMap(): Record<string, string> {
  if (!isBrowser()) {
    return {};
  }

  const raw = window.localStorage.getItem(LEGACY_STORAGE_KEY);

  if (!raw) {
    return {};
  }

  try {
    return JSON.parse(raw) as Record<string, string>;
  } catch {
    return {};
  }
}

function readLegacyCoverIndex(): Record<string, number> {
  if (!isBrowser()) {
    return {};
  }

  const raw = window.localStorage.getItem(COVER_INDEX_KEY);

  if (!raw) {
    return {};
  }

  try {
    return JSON.parse(raw) as Record<string, number>;
  } catch {
    return {};
  }
}

function collectLegacyCoverRecords() {
  if (!isBrowser()) {
    return [] as CoverRecord[];
  }

  const records = new Map<number, CoverRecord>();
  const coverIndex = readLegacyCoverIndex();
  const legacyMap = readLegacyCoverMap();

  Object.entries(legacyMap).forEach(([bookId, dataUrl]) => {
    const numericId = Number(bookId);

    if (!Number.isInteger(numericId) || numericId <= 0 || !dataUrl) {
      return;
    }

    records.set(numericId, {
      bookId: numericId,
      dataUrl,
      updatedAt: coverIndex[bookId] || 0,
    });
  });

  for (let index = 0; index < window.localStorage.length; index += 1) {
    const key = window.localStorage.key(index);

    if (!key?.startsWith(COVER_STORAGE_PREFIX)) {
      continue;
    }

    const numericId = Number(key.slice(COVER_STORAGE_PREFIX.length));
    const dataUrl = window.localStorage.getItem(key);

    if (!Number.isInteger(numericId) || numericId <= 0 || !dataUrl) {
      continue;
    }

    records.set(numericId, {
      bookId: numericId,
      dataUrl,
      updatedAt: coverIndex[String(numericId)] || Date.now(),
    });
  }

  return Array.from(records.values());
}

function clearLegacyStorage() {
  if (!isBrowser()) {
    return;
  }

  window.localStorage.removeItem(LEGACY_STORAGE_KEY);
  window.localStorage.removeItem(COVER_INDEX_KEY);

  const keysToRemove: string[] = [];

  for (let index = 0; index < window.localStorage.length; index += 1) {
    const key = window.localStorage.key(index);

    if (key?.startsWith(COVER_STORAGE_PREFIX)) {
      keysToRemove.push(key);
    }
  }

  keysToRemove.forEach((key) => window.localStorage.removeItem(key));
}

async function ensureLegacyMigration() {
  if (!isBrowser()) {
    return;
  }

  if (!migrationPromise) {
    migrationPromise = (async () => {
      const records = collectLegacyCoverRecords();

      if (!records.length) {
        return;
      }

      const database = await openDatabase();
      const transaction = database.transaction(COVER_STORE_NAME, 'readwrite');
      const store = transaction.objectStore(COVER_STORE_NAME);

      records.forEach((record) => {
        store.put(record);
      });

      await transactionToPromise(transaction);
      clearLegacyStorage();
    })().catch((error) => {
      migrationPromise = null;
      throw error;
    });
  }

  return migrationPromise;
}

async function getCoverRecord(bookId: number) {
  await ensureLegacyMigration();

  const database = await openDatabase();
  const transaction = database.transaction(COVER_STORE_NAME, 'readonly');
  const store = transaction.objectStore(COVER_STORE_NAME);
  const record = await requestToPromise(store.get(bookId) as IDBRequest<CoverRecord | undefined>);

  await transactionToPromise(transaction);
  return record || null;
}

export async function getStoredBookCover(bookId: number): Promise<string | null> {
  if (!isBrowser()) {
    return null;
  }

  const record = await getCoverRecord(bookId);
  return record?.dataUrl || null;
}

export async function setStoredBookCover(bookId: number, coverDataUrl: string) {
  if (!isBrowser()) {
    return;
  }

  await ensureLegacyMigration();

  const database = await openDatabase();
  const transaction = database.transaction(COVER_STORE_NAME, 'readwrite');
  const store = transaction.objectStore(COVER_STORE_NAME);

  store.put({
    bookId,
    dataUrl: coverDataUrl,
    updatedAt: Date.now(),
  } satisfies CoverRecord);

  await transactionToPromise(transaction);
}

export async function removeStoredBookCover(bookId: number) {
  if (!isBrowser()) {
    return;
  }

  await ensureLegacyMigration();

  const database = await openDatabase();
  const transaction = database.transaction(COVER_STORE_NAME, 'readwrite');
  const store = transaction.objectStore(COVER_STORE_NAME);

  store.delete(bookId);

  await transactionToPromise(transaction);
}

function readFileAsDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = () => {
      if (typeof reader.result === 'string') {
        resolve(reader.result);
        return;
      }

      reject(new Error('Unable to read the selected image file.'));
    };

    reader.onerror = () => {
      reject(new Error('Unable to read the selected image file.'));
    };

    reader.readAsDataURL(file);
  });
}

function loadImage(file: File): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const objectUrl = URL.createObjectURL(file);
    const image = new Image();

    image.onload = () => {
      URL.revokeObjectURL(objectUrl);
      resolve(image);
    };

    image.onerror = () => {
      URL.revokeObjectURL(objectUrl);
      reject(new Error('Unable to process the selected image file.'));
    };

    image.src = objectUrl;
  });
}

function compressImage(image: HTMLImageElement) {
  const scale = Math.min(
    1,
    MAX_COVER_WIDTH / image.naturalWidth,
    MAX_COVER_HEIGHT / image.naturalHeight,
  );
  const width = Math.max(1, Math.round(image.naturalWidth * scale));
  const height = Math.max(1, Math.round(image.naturalHeight * scale));
  const canvas = document.createElement('canvas');

  canvas.width = width;
  canvas.height = height;

  const context = canvas.getContext('2d');

  if (!context) {
    throw new Error('Unable to prepare the selected image file.');
  }

  context.imageSmoothingEnabled = true;
  context.imageSmoothingQuality = 'high';
  context.drawImage(image, 0, 0, width, height);

  const webpDataUrl = canvas.toDataURL('image/webp', COVER_QUALITY);

  if (webpDataUrl.startsWith('data:image/webp')) {
    return webpDataUrl;
  }

  return canvas.toDataURL('image/jpeg', COVER_QUALITY);
}

export async function readImageFileAsDataUrl(file: File): Promise<string> {
  if (!isBrowser()) {
    return readFileAsDataUrl(file);
  }

  try {
    const image = await loadImage(file);
    return compressImage(image);
  } catch {
    return readFileAsDataUrl(file);
  }
}
