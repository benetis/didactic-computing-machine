import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ProductsComponent} from './products.component';
import {ItemDetailsComponent} from './components/product-details/product-details.component';
import {ItemListComponent} from './components/product-list/product-list.component';

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
    component: ItemDetailsComponent
  }
  // {
  //   path: 'lazy',
  //   loadChildren: './lazy/lazy.module#LazyModule'
  // }
];
