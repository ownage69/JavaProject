import { useEffect, useState } from 'react';
import { getStoredBookCover } from '../services/bookCoverStorage';

export function useStoredBookCover(bookId: number | null | undefined) {
  const [coverUrl, setCoverUrl] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    if (!bookId) {
      setCoverUrl(null);
      return () => {
        cancelled = true;
      };
    }

    getStoredBookCover(bookId)
      .then((value) => {
        if (!cancelled) {
          setCoverUrl(value);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setCoverUrl(null);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [bookId]);

  return coverUrl;
}
