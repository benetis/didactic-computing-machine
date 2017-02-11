import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LandingComponent} from './landing.component';
import { ItemListComponent } from './components/item-list/item-list.component';
import { ItemComponent } from './components/item/item.component';
import { ItemFilterComponent } from './components/item-filter/item-filter.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    LandingComponent,
    ItemListComponent,
    ItemComponent,
    ItemFilterComponent
  ],
  exports: [
    LandingComponent
  ]
})
export class LandingModule {
}
