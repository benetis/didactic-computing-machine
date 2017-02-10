import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Item} from '../../classes/item';

@Component({
  selector: 'spa-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class ItemListComponent implements OnInit {

  @Input() public items: Item[] = [];

  constructor() { }

  ngOnInit() {
  }

}
