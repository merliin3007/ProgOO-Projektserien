# Documentation

| Java Version | IDE |
|--------------|-----|
| OpenJDK 18   | intelliJ IDEA, Visual Studio Code |

Most documentation can be found in code with javadoc or normal comments.
We use a static constant within the package called `Address.NOTGIVEN` to indicate an integer value of the address (zipcode or housenumber) was not given by the user. String values are just left empty.

The Utility-Class provides Scanner-Operations for all usecases. This is done to avoid any scanner weirdness which was previously experienced during debugging.

The abstract Class `Contact` is used to be able to treat both type of contacts as if they were of the same type. The only methods and attributes of both contact types that can fit in the abstract class are address and it's accessors.
`name` is of different type in both classes, while `companyName` is only used in the `CompanyContact`. Furthermore it would be confusing if `name` and `companyOwner` were also used in the abstract class as accessing them would then lead to naming issues. 

The addressBook is used via the terminal. Type `help` fore more information.