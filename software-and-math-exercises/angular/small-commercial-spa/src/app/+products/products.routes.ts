import {Routes} from '@angular/router';
import {ProductsComponent} from './products.component';
import {ProductDetailsComponent} from './components/product-details/product-details.component';
import {ShoppingCartComponent} from './components/shopping-cart/shopping-cart.component';

export const routes: Routes = [
  {
    path: 'products',
    component: ProductsComponent
  },
  {
    path: 'details/:id',
    component: ProductDetailsComponent
  },
  {
    path: 'cart',
    component: ShoppingCartComponent
  }
];
