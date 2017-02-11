import { browser, element, by } from 'protractor';

export class SmallCommercialSpaPage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('spa-root h1')).getText();
  }
}
