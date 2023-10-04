# SDS Core

The SDA project was conceived in 2008 and aims to produce Java libraries that (ultimately) support parsing, validation and transformation of SDA content. The SDS core library supplies a parser to read SDA schema and a validator to validate SDA input against it. It depends on the [SDA core](https://github.com/hclbaur/sda-core) library.

## What is SDS

SDS is to SDA what XSD is to XML. In other words, it is a schema language that allows you to specify what your input SDA should look like. The schema can subsequently be used to *validate* the input before you run into problems processing something that is incomplete, incorrect, or unknown.

For example, if this is your input:

	addressbook {
		contact "1" {
			firstname "Alice"
			phonenumber "06-11111111"
			phonenumber "06-22222222"
		}
		contact "2" {
			firstname "Bob"
			phonenumber "06-33333333"
			phonenumber "06-44444444"
		}
	}

Then this could be the schema to properly describe it:

	schema {
		node "addressbook" {
			node "contact" {
				type "integer" occurs "0..*"
				node "firstname" { type "string" }
				node "phonenumber" { type "string" occurs "1..*" }
			}
		}
	}

As you can see the specification is rather straight-forward if you are already familiar with SDA and the concept of schema. The example defines an 'addressbook' as a collection of zero or more 'contact' nodes, which have an integer "id" and should contain a 'firstname' node followed by one or more 'phonenumber' nodes. 

There is a lot more to be said about SDS than this, and I will do so in the [tutorial](docs/TUTORIAL.md).

## Running the demo

Please read up on [SDA](https://github.com/hclbaur/sda-core#what-is-sda), run the demo, then come back here for the SDS demo.

For the SDS demo, get `demo.jar` and `addressbook.sds`  from the latest [release](https://github.com/hclbaur/sds-core/releases/latest) and copy them to where you ran the SDA demo. Assuming the java executable is in your path, run the demo like this:

	java -jar demo.jar addressbook.sds addressbook.sda
	
which will generate the following (familiar) output:

	Alice has 2 phone number(s).
	  Number 1: 06-11111111
	  Number 2: 06-22222222
	Bob has 2 phone number(s).
	  Number 1: 06-33333333
	  Number 2: 06-44444444

When you look at the [code](src/main/java/demo.java) you will see that the demo creates a schema from the `addressbook.sds` file and uses that to validate the data in `addressbook.sda` prior to processing it.

In this case the addressbook is *valid*. But we also had a bad addressbook that caused problems when it was processed. Let's try that again now:

	java -jar demo.jar addressbook.sds badbook.sda

Your output should now look like this:

	/addressbook/contact[1]/phonynumber: got 'phonynumber', but 'phonenumber' was expected
	/addressbook/contact[2]/phonenumber[1]: got 'phonenumber', but 'firstname' was expected
	
As you can see, the validation uncovered two serious issues with your addressbook, and as a result, the demo refuses to process it, lest it causes a catastrophe. 

I hope this demonstrates the use of SDS as a valuable companion to SDA. You may be interested to know that there is another one called [SDT](https://github.com/hclbaur/sdt-core) which supplies transformation and XPath capabilities.

----
