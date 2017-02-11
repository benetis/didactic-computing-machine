import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LandingComponent} from './+landing/landing.component';


export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: LandingComponent
  },
  // {
  //   path: 'lazy',
  //   loadChildren: './lazy/lazy.module#LazyModule'
  // }
];
