# SDS Core

The SDA project was conceived in 2008 and aims to produce Java libraries 
that (ultimately) support parsing, validation and transformation of SDA 
content. The SDS core library supplies a parser to read SDA schema and a
validator to validate SDA input against it. 
It depends on the [SDA core](https://github.com/hclbaur/sda-core) library.

## What is SDS

SDS is to SDA what XSD is to XML. In other words, it is a schema language 
that allows you to specify what your input SDA should look like. The schema 
can subsequently be a used to *validate* the input before you run into 
problems - which may happen if it's incomplete, incorrect, or unknown.

For example:

	schema {
		node "addressbook" {
			node "contact" {
				occurs "0..*"
				node "firstname" { type "string" }
				node "phonenumber" { type "string" occurs "1..*" }
			}
		}
	}

As you can see the specification is rather straight-forward if you are already 
familiar with SDA and the concept of schema. The example defines an 'addressbook'
as a collection of zero or more 'contact' nodes, each of which should contain a 
'firstname' followed by one or more 'phonenumber' nodes. 

There is a lot more to be said about SDS than this, so please refer to the 
[documentation](docs/) for a complete specification.

## Running the demo

Please read up on [SDA](https://github.com/hclbaur/sda-core#what-is-sda), run 
the demo, then come back here for the SDS demo.

To run the demo, download sds-core.jar, demo.class and the addressbook.sds file 
from the latest [release](https://github.com/hclbaur/sds-core/releases/latest)
and copy them to where you ran the SDA demo (overwriting the demo.class).

Assuming the java executable is in your path, run the demo like this:

	java -cp .;sda-core.jar;sds-core.jar demo addressbook.sds addressbook.sda
	
which will generate the following (familiar) output:

	Alice has 2 phone number(s).
	  Number 1: 06-11111111
	  Number 2: 06-22222222
	Bob has 2 phone number(s).
	  Number 1: 06-33333333
	  Number 2: 06-44444444

When you look at the [code](src/test/java/demo.java) you will see that the demo 
parses the `addressbook.sds` file and uses the resulting schema to validate the 
data in `addressbook.sda` prior to processing it.

In this case the addressbook is *valid*. But we also had a bad addressbook that 
caused problems when being processed. Let's try that again now:

	java -cp .;sda-core.jar;sds-core.jar demo addressbook.sds badbook.sda

Your output should look like this:

	/addressbook/contact[1]/phonynumber: got 'phonynumber', but 'phonenumber' was expected
	/addressbook/contact[2]/phonenumber[1]: got 'phonenumber', but 'firstname' was expected
	
As you can see, the validator found two issues with the data in the addressbook, 
and as a result, the demo refuses to process it, lest it causes a catastrophe. 

I hope this demonstrates the use of SDS as a valuable companion to SDA.

----
