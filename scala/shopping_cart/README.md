# From practical FP in scala
```
• A guest user should be able to:
– register into the system with a unique username and password.
– login into the system given some valid credentials.
– see all the guitar catalog as well as to search by brand.
• A registered user should be able to:
– add products to the shopping cart.
– remove products from the shopping cart.
– modify the quantity of a particular product in the shopping cart.
– check out the shopping cart, which involves:
∗ sending the user Id and cart to an external payment system (see below).
∗ persisting order details including the Payment Id.
– list existing orders as well as retrieving a specific one by Id.
– log out of the system.
• An admin user should be able to:
– add brands.
– add categories.
– add products to the catalog.
– modify the prices of products.
• The frontend should be able to:
– consume data via an HTTP API that we need to define.
```