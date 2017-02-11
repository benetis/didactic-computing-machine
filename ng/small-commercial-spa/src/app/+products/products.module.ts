import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ProductsComponent} from './products.component';
import { ItemListComponent } from './components/item-list/item-list.component';
import { ItemComponent } from './components/item/item.component';
import { ItemFilterComponent } from './components/item-filter/item-filter.component';
import { ItemDetailsComponent } from './components/item-details/item-details.component';
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
