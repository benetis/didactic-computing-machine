export type ItemColor = 'blue' | 'green' | 'gray';

export interface Item {
  id?: number;
  name?: string;
  price?: number;
  currency?: string;
  colors?: ItemColor[];
  picture?: string;
}
