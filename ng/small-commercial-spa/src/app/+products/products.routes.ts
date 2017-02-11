import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ProductsComponent} from './products.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ProductsComponent
  },
  // {
  //   path: 'lazy',
  //   loadChildren: './lazy/lazy.module#LazyModule'
  // }
];
