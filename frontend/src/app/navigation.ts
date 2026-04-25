import type { LucideIcon } from 'lucide-react';
import {
  ArrowLeftRight,
  BookOpen,
  Feather,
  LayoutGrid,
  Shapes,
  Building2,
  Users,
} from 'lucide-react';

export interface NavigationItem {
  label: string;
  to: string;
  description: string;
  icon: LucideIcon;
}

export const navigationItems: NavigationItem[] = [
  {
    label: 'Dashboard',
    to: '/dashboard',
    description: 'Overview and activity',
    icon: LayoutGrid,
  },
  {
    label: 'Books',
    to: '/books',
    description: 'Catalog and copy counts',
    icon: BookOpen,
  },
  {
    label: 'Authors',
    to: '/authors',
    description: 'Writer records',
    icon: Feather,
  },
  {
    label: 'Categories',
    to: '/categories',
    description: 'Shelf subjects',
    icon: Shapes,
  },
  {
    label: 'Publishers',
    to: '/publishers',
    description: 'Publisher records',
    icon: Building2,
  },
  {
    label: 'Readers',
    to: '/readers',
    description: 'Reader records',
    icon: Users,
  },
  {
    label: 'Loans',
    to: '/loans',
    description: 'Borrow and return',
    icon: ArrowLeftRight,
  },
];
