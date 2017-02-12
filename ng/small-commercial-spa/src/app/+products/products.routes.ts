import {Routes} from '@angular/router';
import {ProductsComponent} from './products.component';
import {ProductDetailsComponent} from './components/product-details/product-details.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'list'
  },
  {
    path: 'list',
    component: ProductsComponent
  },
  {
    path: 'details/:id',
    component: ProductDetailsComponent
  }
];
