import axios from 'axios';
import type { ApiErrorResponse } from '../types/api';

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return (
      error.response?.data?.message ||
      error.response?.data?.errors?.[0]?.message ||
      error.message ||
      'Request failed.'
    );
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Something went wrong.';
}

export function getValidationMessage(
  fieldName: string,
  errors: ApiErrorResponse | null | undefined,
): string | undefined {
  return errors?.errors?.find((item) => item.field === fieldName)?.message;
}
