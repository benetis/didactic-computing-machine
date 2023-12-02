/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ColorListComponent } from './color-list.component';

describe('ColorListComponent', () => {
  let component: ColorListComponent;
  let fixture: ComponentFixture<ColorListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ColorListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ColorListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
