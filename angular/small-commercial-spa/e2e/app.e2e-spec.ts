import { SmallCommercialSpaPage } from './app.po';

describe('small-commercial-spa App', function() {
  let page: SmallCommercialSpaPage;

  beforeEach(() => {
    page = new SmallCommercialSpaPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
