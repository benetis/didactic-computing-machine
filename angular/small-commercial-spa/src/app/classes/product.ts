export type ProductColor = 'blue' | 'green' | 'gray';

export interface Product {
  id?: number;
  name?: string;
  price?: number;
  currency?: string;
  colors?: ProductColor[];
  picture?: string;
}

export interface ProductFilter {
  color?: ProductColor;
}
