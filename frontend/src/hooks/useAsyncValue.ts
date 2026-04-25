import { useEffect, useState, type DependencyList } from 'react';
import type { AsyncValue } from '../types/api';
import { getErrorMessage } from '../utils/errors';

export function useAsyncValue<T>(
  loader: () => Promise<T>,
  dependencies: DependencyList = [],
): AsyncValue<T> & { reload: () => void } {
  const [value, setValue] = useState<AsyncValue<T>>({
    data: null,
    loading: true,
    error: null,
  });
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let cancelled = false;

    setValue((current) => ({
      data: current.data,
      loading: true,
      error: null,
    }));

    loader()
      .then((data) => {
        if (!cancelled) {
          setValue({
            data,
            loading: false,
            error: null,
          });
        }
      })
      .catch((error) => {
        if (!cancelled) {
          setValue({
            data: null,
            loading: false,
            error: getErrorMessage(error),
          });
        }
      });

    return () => {
      cancelled = true;
    };
  }, [reloadKey, ...dependencies]);

  return {
    ...value,
    reload: () => setReloadKey((current) => current + 1),
  };
}
