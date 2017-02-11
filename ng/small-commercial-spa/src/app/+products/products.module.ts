import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ProductsComponent} from './products.component';
import { ItemListComponent } from './components/product-list/product-list.component';
import { ItemComponent } from './components/product/product.component';
import { ItemFilterComponent } from './components/product-filter/product-filter.component';
import { ItemDetailsComponent } from './components/product-details/product-details.component';
import {RouterModule} from '@angular/router';
import {routes} from './products.routes';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ],
  declarations: [
    ProductsComponent,
    ItemListComponent,
    ItemComponent,
    ItemFilterComponent,
    ItemDetailsComponent,
  ],
  exports: [
    ProductsComponent
  ]
})
export class ProductsModule {
}
