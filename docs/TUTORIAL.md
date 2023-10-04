# SDS Tutorial

- [Introduction](/docs/TUTORIAL.md#introduction)
- [Why we need it](/docs/TUTORIAL.md#why-we-need-it)
- [It is all about looks](/docs/TUTORIAL.md#it-is-all-about-looks)
	- [First contact](/docs/TUTORIAL.md#first-contact)
	- [Re:mix](/docs/TUTORIAL.md#re:mix)
	- [Dolls, salami and blinds](/docs/TUTORIAL.md#dolls-salami-and-blinds)
	- [Finding the root](/docs/TUTORIAL.md#finding-the-root)
	- [More on multiplicity](/docs/TUTORIAL.md#more-on-multiplicity)
- [Typicalities](/docs/TUTORIAL.md#typicalities)
	- [Matters of space](/docs/TUTORIAL.md#matters-of-space)
	- [Facets of restriction](/docs/TUTORIAL.md#facets-of-restriction)
	- [The nothing that is](/docs/TUTORIAL.md#the-nothing-that-is)
	- [Anything goes](/docs/TUTORIAL.md#anything-goes)
- [Model citizens](/docs/TUTORIAL.md#model-citizens)
	- [Group mentality](/docs/TUTORIAL.md#group-mentality)
	- [The choice is yours](/docs/TUTORIAL.md#the-choice-is-yours)
	- [Order! Order!](/docs/TUTORIAL.md#order-order)
- [Conclusion](/docs/TUTORIAL.md#conclusion)


## Introduction

This tutorial will teach you the SDS syntax, version 2. Compared to version 1, I 
dropped the `name` attribute, and used the SDA 2 syntax to name types in a more 
intuitive and appealing way. Other than that, there are no suprises. You can find 
the original [here](/docs/SDS1.md).


## Why we need it

Why do we need schema? Well, for starters, a schema allows you to *validate*  
data before you process it. This enables you to detect and mitigate  anomalies 
before they wreak havoc in your precious application, system of record, or 
business process.

Another reason is that it makes developing data processing software easier 
because schema can show you at design-time exactly what you are working with. In 
fact, schema allows code generators to eliminate a lot of the software “writing” 
part.

And finally, it makes it easy to share information about your data (and how to 
process it) without having to write lengthy specifications.

This is true regardless of whether the data is “encoded” as EDI (oh, the days of
 yore), XML, JSON, or – in fact - SDA.


## It is all about looks

What does SDA schema (SDS for short) look like? Well, it looks like SDA; because 
that is what it *is*. This follows the same approach as XML schema (or XSD)  
which is usually written in XML and JSON schema, which can be written in JSON (or 
YAML, but that is evil).

The good thing about this approach is that you don’t have to learn another 
“language” to read SDS, and I don't have to write a parser from scratch to 
process it (ok, that's the real reason).

The downside is (at least in the case of SDS) that I am limited by the syntax of 
SDA, which rules out a few design options. Some things would be easier if SDA 
supported namespaces (but I decided not to go there, bummer).

### First contact

Let's consider the following:

	addressbook {
		contact {
			firstname "Alice"
			phonenumber "06-21438709"
		}
		contact {
			firstname "Bob"
			phonenumber "06-90784523"
		}
	}

This SDA content could be used to store your contacts (yes, all two of them). 
Now assume that we are creating an application that can import bulk contact 
information in this format and we want to make sure that what we are importing 
is in the expected format.

Or maybe we already have such an application, and someone wants to send us a 
large number of contacts in a file format we can handle. Luckily, they support 
SDA (what are the chances).

In either case, a schema that describes the address book structure would be 
really helpful. In SDS, it would look like this:

	schema {
		node "addressbook" {
			node "contact" {
				occurs "0..*"
				node "firstname" { type "string" }
				node "phonenumber" { type "string" }
			}
		}
	}

The tags (`schema`, `node`, `type`, `occurs`) are part of the SDS vocabulary, and 
the values (in quotes) define your particular use case. The schema defines the 
content model in terms of *components* such as node (type) definitions or model 
groups (which we will meet later). Components have *attributes* that determine 
what type of content is allowed and how often a node may occur in it's parent 
context. We will meet more attributes later.

In this example, the schema defines an address book as an SDA node named 
'addressbook' that contains another node named 'contact', which in turn has two 
nodes named 'firstname' and 'phonenumber'. The latter two are usually called 
*simple* types because they have no child node definitions, as oppposed to the 
addressbook and contact, which are *complex* types (defining a parent node).

We also infer (from the `occurs` attribute) that there can be more than one 
contact, with a minimum of 0 – so the book can be empty - and no specified 
maximum, which is indicated by the asterisk. In other words, contacts are 
*optional* and may be *repeating*.

The two nodes that hold the actual contact information (first name and phone 
number) have default multiplicity, which means they must occur exactly once. In 
order words, they are *mandatory* for a contact. It is safe to assume that 
everybody has a name and without a phone you have no business being in the book 
anyway.

The final observation we make is that both name and phone number are of type 
`string`, which means they can be practically anything (for a real use case 
this may be not the best design choice).

And that is all. Well, it's not of course. It never is. But conceptually, 
this is the gist of it. The remainder of this document will re-iterate and 
discuss this, and more, in pain-staking detail (rest assured).

### Re:mix

Have another look at the type defintion of a contact:

	… 
		node "contact" {
			occurs "0..*"
			node "firstname" { type "string" }
			node "phonenumber" { type "string" }
		}
	…

As mentioned before, we call this a complex type because it defines a parent 
node, with complex content (other nodes). However, we know that the SDA syntax 
allows nodes to have both simple *and* complex content, so what if we want a 
contact to have an index number, such as:

	… 
		contact "123" {
			firstname "Alice" 
			phonenumber "06-21438709"
		}
	… 

To accomplish this, just add a type attribute specifying that the contact node 
has an `integer` value: 

	… 
		node "contact" {
			type "integer" occurs "0..*"
			node "firstname" { type "string" }
			node "phonenumber" { type "string" }
		}
	…

I suppose we could call this a *mixed* type.

### Dolls, salami and blinds

Have another look at the schema we started with:

	schema {
		node "addressbook" {
			node "contact" {
				occurs "0..*"
				node "firstname" { type "string" }
				node "phonenumber" { type "string" }
			}
		}
	}

As you can see, it defines the data structure in a top-down fashion, starting 
with the root node 'addressbook' and  recurses into the child nodes until the 
entire SDA document is specified.

In XML schema, this pattern is called “Russian Doll” after the decorative dolls 
that contain smaller dolls, containing even smaller ones, and so forth.

An alternative way to accomplish the same thing in another way, is to declare 
small “building blocks” that make up your data and refer to these in order to 
build the bigger structure(s) in your document.

This pattern is called “Salami Slice” or “Venetian Blind”, depending on whether 
your building blocks are declared as elements or types. In SDS, that distinction 
is implicit; a node declared in the main schema section is a re-useable type, 
that can be referred to by other nodes. I will liberally use the terms node and 
type interchangeably.

Applying this pattern to the address book schema, here is what that looks like:

	schema {
		node "contact" {
			node "firstname" { type "string" }
			node "phonenumber" { type "string" }
		}
		node "addressbook" {
			node { type "contact" occurs "0..*" }
		}
	}

As you can see, we moved the definition of the contact to the main section and 
made a type reference in the 'addressbook' node. The name of the defined node 
will default to the name of the type ('contact').

What we achieved is re-usability of the contact definition. Granted, in this 
example that is not a real issue, but just to make my point, I modified the 
address book so I can store my own contact details in an 'owner' node:

	…
		node "addressbook" {
			node "owner" { type "contact" }
			node { type "contact" occurs "0..*" }
		}
	…

I named it 'owner' to set this node apart from regular contacts, and since 
there is only one owner, I omitted the multiplicity to make it singular and 
mandatory. The book then, could look like this:

	addressbook {
		owner {
			firstname "Harold"
			phonenumber "06-52695869"
		}
		contact {
			firstname "Alice"
			phonenumber "06-21438709"
		}
		…
	}

We could take this even further, by moving out simple types to the main 
section as well:

	schema {
		node "phonenumber" { type "string" }
		node "contact" {
			node "firstname" { type "string" }
			node { type "phonenumber" }
		}
		…
	}

In general, this has little benefit for a simple type, unless it is customized 
and re-used a lot. For instance, the phone number could be restricted to allow 
only sensible values, and act as a super-type for say, a home number or a mobile 
number.

### Finding the root

Looking at the “Russian Doll” style schema, it is visually clear that we intend 
'addressbook' to be the root node of the document, since all other nodes are 
*local* to it.

When moving out types to the main schema section, this is no longer apparent, 
because each of them could – at least in theory - act as a root node.

Depending on the number of nodes in the main section that may be a problem, or at 
least confusing. If you want to designate the root node (or default type), you can 
do so by adding the `type` attribute in the schema node:
	
	schema {
		type "addressbook"
		node "contact" {
			node "firstname" { type "string" }
			node "phonenumber" { type "string" }
		}
		node "addressbook" {
			node { type "contact" occurs "0..*" }
		}
	}

It doesn’t really matter where the default type is located and whether nodes are 
declared in order of their “appearance” or not.

### More on multiplicity

Here is a list of the most common values that the `occurs` attribute accepts:

**1..1** (or just **1**): this is the default, and equivalent to omitting 
multiplicity altogether. It means the node is singular and mandatory – it must 
occur exactly once.

**0..1**: this also indicates a singular node, but presence is optional. It
 can occur at most once.

**1..\***: indicates mandatory and unbounded presence; the node must occur
 at least once.

**0..\***: means optional and unbounded. The node may occur any number of
 times or not at all.

Not often will you come across a node that must occur – say - exactly 2 times,  
or between 4 and 8 times. But if you have this requirement, the general syntax 
for specifying multiplicity is

**n..m**: where n and m are both non-negative integers and **n** <= **m**,

**n..\***: where \* means unbounded occurrence, or

**n**: which means the node must occur exactly **n** times.

This includes **0** (or **0..0**), which means the node may not occur at all. 
That makes little sense, as you would not declare such a node in the first 
place. You can think of this as a way to “comment out” the type without having 
to remove it from the schema.

One final thing about multiplicity (one which you may feel is trivial) is that 
it is not an intrinsic property of a type but rather one that depends on the 
current context. A contact is not repeatable because you expect to have more 
than one, but because you usually have more than one *in an address book*.

Likewise, there is nothing inherently singular about an address book, as you 
could have many of them by extending the schema like this:

	node "shelf" {
		node { type "addressbook" occurs "0..*" }
	}

Enough already, let’s go on to types!


## Typicalities

So far, we primarily saw a `string` type in our schema definition. We are going 
to need a lot more than that, if our data is to be correctly interpreted. Without 
further ado, here are the value types supported by SDS:

**string**: the general purpose type, without restrictions other than that
 the data is text (characters in the current encoding).

**binary**: a type suitable for binary data, represented as a string (MIME
 base64 encoding).

**integer**: this type implies that your data is to be interpreted as a
 positive or negative number without a fractional part, and zero.

**decimal**: a general numeric type, that includes the aforementioned integer
 type, and decimal as well as scientific representations.

**date**: a type for a calendar date, and **datetime** for a specific point
 in time.	

**boolean**: the well-known truth value type (true or false).

Compared to the myriad of types and sub-types that are supported in XML schema, 
this may seem limited. That is true; both SDA and SDS are a trade-off between 
versatility and simplicity (or necessity). And that approach can be limiting in 
some ways. On the other hand, when was the last time you actually used a 
`gMonthDate` type?

Having said that, you can use certain attributes called *facets* (covered later)  
to restrict any of the built-in types and mimic most of the derived types that 
you feel are conspicuously absent.

But before we go there, I should explain the difference between the so-called 
"value space" and "lexical space".

### Matters of space

The value space is the set of distinct and valid values for a particular type. 
The lexical space is the set of valid *representations* of that value. An example 
will help to clarify this: 42 is a single value from the value space of a number. 
However, it may be represented as “42”, or “42.0”, or as “forty-two”, or “the 
answer to life, the universe and everything”.

With that in mind, we will shortly review the SDS types listed at the beginning 
of this chapter, except the string type which has no restrictions in either 
space.

#### binary

The lexical space of this type allows both a single line representation and a 
“formatted” one with line-breaks and possibly other white-space. For example:

	logo {
		icon "iVBORw0KGgoAAAANSUhEUgAAAAsAAAAUCAYAAABbLMdoAAAAAXNSR0IArs4c6QAAAARnQU1BAACx
		jwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAECSURBVDhPdZLLrYQwDEXNpwIQS8QGGqA5GqAO
		CkCIEqiCLWsWSOwRH0/scd4jYziSiW9yHZwo3nVdCDc8z5MMwKyB7/uA+LX4/BWssSgKzoMggK7r
		/jegnW0QZgqbpuHcYtf/zMQwDJjnOed2/h5OG9M0QVmWojSOmXo8jkOURplNB6I0jnmeZ4jjWNQD
		1LjFSDQFnP8ejoJ+i9u2sbFt21cjBbex7zsNEEURj69QhcVIXNeV899dKXjnrw+grmuoqorzJ5zb
		SNMUlmURpXHM53nyK3tDmcMwFKVxzFmWwTiOoh64n5YwU9j3PecWu67MRJIkXERhHj/PKfO9gHDv
		/MIPGRRsF7jd6noAAAAASUVORK5CYII=" 
		filename "curlybrace.png"
	}

#### integer

The lexical space of this type is identical to its value space. That is, it 
allows integers to be represented in their canonical form only, such as “-2”, 
“0” or “4”.

Note that this is different from the XSD schema integer type, which allows 
values like “+007” and “-0”. If you need that kind of freedom, you should use 
the decimal type instead.

#### decimal

The lexical space of this type includes everything that can be unambiguously 
interpreted as a (rational) number. It includes all integers (with or without a 
leading + or - sign or 0’s), all decimals with a dot separating the fractional 
part, as well as scientific E notation.

For example: “42”, “-.27315”, “+0.314159”, “6.022E23”, or “-1.602e-19”.

#### date

This is a type representing a Gregorian calendar date, intended for practical 
use. Its lexical space is the familiar “YYYY-MM-DD” notation. It supports neither 
negative dates (B.C.) nor dates beyond the year 9999. Also, it lacks a time zone, 
which – without knowing the time - is of limited use anyway.

For example, when I tell you I am born on “1968-02-28”, it goes without saying 
that for some people on this planet it may have been the 27<sup>th</sup>, or the 
29<sup>th</sup> of February depending on the time I was born, which – by the way 
– I do not know. Adding a time zone hardly narrows it down, much like my father 
always says he was “at the pub” when my mother gave birth.

#### datetime

To make up for the inherent limitations of our `date` type, SDS supports the 
datetime type, which identifies a specific point in time and has a lexical space
 more-or-less defined by the well-known ISO 8601 extended format: [-]
CCYY-MM-DDThh:mm:ss\[.fff](Z|(+|-)hh:mm).

I say more-or-less, because there is a price to pay for accuracy. Unlike in 
the real ISO 8601 format, in SDS the time zone is **mandatory**. This may 
seem harsh, but it fixes a common problem in data exchange: ambiguous time.

Whether you assume UTC or local time, it is unlikely everyone will make the same
 assumption all of the time, causing all kinds of exotic issues in applications.

#### boolean

And finally, the humble Boolean, the lexical space of which allows only “true” 
or “false” in lowercase only - and “0” or “1” are not accepted either!

### Facets of restriction

Facets are attributes to restrict the built-in types and create sub-types as it 
were. The imperative word here is *restrict*; there is no way you can extend the 
value or lexical space of any type using a facet. Here are the facets at your 
disposal:

#### length

This facet applies to string and binary types. The restriction on length is 
specified in the syntax we encountered when talking about multiplicity. Note that 
for a string type, length is the number of characters, whereas for binary types 
it is the number of *bytes*. Here are a few examples:

	node "char3" { type "string" length "3" }

	node "nonEmptyString" { type "string" length "1..*" }

	node "imageMax1MB"{ type "binary" length "0..1048576" }

Omitting the length facet is equivalent to **0..\***.

#### value

The value facet restricts the value space of integer, decimal and date(time) 
types. The syntax is similar to that for length and multiplicity, except that  
standard interval notation is used, unless a single value is supplied. Limits 
can be inclusive or exclusive depending on whether brackets (inclusive) or 
parentheses (exclusive) are used:

	node "negativeInteger" { type "integer" value "(*..0)" }

	node "signedByte" { type "integer" value "[-128..128]" }

	node "temperature" { type "decimal" value "[-273.15..*)" }

	node "PI" { type "decimal" value "3.1415926535" }

	node "myBirthday" { type "date" value "1968-02-28" }

	node "thisYear" { type "date" value "[2020-01-01..2021-01-01)" }

	node "now" { type "datetime" value "2020-08-12T11:46:00+02:00" }

	node "today" { type "datetime"
		value "[2020-08-12T00:00:00+02:00..2020-08-13T00:00:00+02:00)"
	}

Omitting the value facet is equivalent to **(\*..\*)**.

#### pattern

The final facet is different from length and value in two ways: first, it  
works on all simple types, and second, it restricts the lexical space rather 
than the value space. A pattern lets you derive all kinds of custom types 
using the power of a regular expression. The possibilities are literally 
endless, so here are just a few to whet your appetite:

	node "token" { type "string" pattern "[^\\s]" }

	node "time24" { type "string" pattern "([01][0-9]|2[0-3]):[0-5][0-9]" }

	node "primaryColour" { type "string" pattern "red|yellow|blue" }

In the first example we need to escape the backslash because it is the SDA 
escape character. The last example illustrates how to create an enumeration 
using a pattern.

### The nothing that is

This is a perfect opportunity for me to rant about empty nodes and why you 
should try to avoid them. Oh wait, I did 
[that](https://eai-dev.blogspot.com/2019/06/empty-elements-in-xml-much-ado-about.html) 
years ago.

So, while there is nothing “illegal” about empty nodes, you should not get in the 
habit of creating them if it can be avoided. Not merely because it is  a waste of 
bandwidth, but also because funny things can happen when they are processed by 
non-suspecting applications.

Schema validation will help you catch emptiness when it is potentially 
dangerous. For example, if somewhere in your schema you have this:

	…
	node "age" { type "integer" occurs "0..1" }
	…

And the data you are validating against that schema looks like this:

	…
	personal { age "" gender "male" }
	…

then validation would reject the empty 'age' node. Why? Because “nothing” is not 
an integer. Nor is it a date, or a Boolean, or anything other than an empty 
string (which is fine for a `string` type).

In this case, since age was declared optional, the right thing to do would be to 
omit it altogether. So rather than empty, it would be absent, and therefor unknown.

But what if age had been mandatory, or if you truly wanted (needed) to transmit 
an empty value? Maybe the empty node could not be avoided due to a technical 
restriction. Or perhaps you wanted the recipient to explicitly clear (nullify) 
the age record in a database.

In that case you could define age as a `string` – which would work but also 
defeat the purpose of data typing and validation. A better solution is to allow 
it to be empty (or null[^2]):

	node "age" { type "integer" nullable "true" }

Presto, you have just allowed age to be empty. However, I feel obliged to 
point out that you should use this with care (so not unless there is a good 
reason for it).

Now, about strings. These can be empty practically by definition, but if that 
is not what you want, you can use the length or pattern facet to require at 
least one character. Or alternatively you just say:

	node "id" { type "string" nullable "false" }

[^2]: There are really interesting discussions on the Internet whether it 
should  be nil or null, and what that value actually represents. I decided to 
run with null and nullable, rather than nil and nillable.

Note that the concept of nullability applies to simple content only.

### Anything goes

Before we leave the subject of types and restrictions, there is one more 
special “type” we need to discuss. So far, schema has enabled us to specify 
exactly what our data looks like and what type of information we expect. But 
suppose we want to define a structure that can have any content, which we 
have no prior knowledge about?

This would be useful – for example - if we want to allow others to include 
“custom” data, which is not governed by our schema. For instance, a contact 
sending you an electronic calling card might want to include some personal 
information, for instance in an 'about' node:

	contact {
		firstname "Bob" phonenumber "06-90784523"
		about {
			hobbies { hobby "fishing" hobby "reading" }
			favoritecolour "blurple"
		}
	}

Since there is no way of knowing in advance what people like to share (if 
anything at all), we define the contact like this:

	node "contact" {
		occurs "0..*"
		node "firstname" { type "string" }
		node "phonenumber" { type "string" }
		node "about" {
			occurs "0..1"
			node { type "any" occurs "1..*" }
		}
	}

This specifies that the phone number is optionally followed by an 'about' node, 
containing one or more nodes with any name, of any type. Note, this is slightly 
different from:

		…
		node "phonenumber" { type "string" }
		node "about" { type "any" occurs "0..1" }
		…

because when it is specified like that, the 'about' node is not necessarily a 
complex type, but it can be a simple (or mixed) type too, for example:

	contact {
		firstname "Bob" phonenumber "06-90784523"
		about "I like to fish and read books."
	}


It is never a bad idea to confine “undefined” content to a (complex) node 
with a known name, like in the examples above. But it does not need to 
be. When the contact is defined like this:

	node "contact" {
		occurs "0..*"
		node "firstname" { type "string" }
		node "phonenumber" { type "string" }
		node { type "any" occurs "0..*" }
	}

then any number of nodes, of any name and any type may follow the phone number 
(which is probably not the best design choice). Also, these wildcard nodes should 
preferably come after the regular types, because the validation process may not 
be able to disambiguate the content otherwise.

## Model citizens

So far, we saw only one way to build a content model; by defining nodes within 
other nodes and making them optional or mandatory, and possibly repetitive. 
Although this will get you a long way, some patterns are cumbersome or even 
impossible to express without more advanced features. These *model* *groups* will 
be the subject of this chapter.

### Group mentality

First, there is the (sequence) `group`, which you use to group nodes that have 
an interdependence. A (somewhat contrived) example will explain this better.

Assume that we want to model a contact with at least a first name, maybe a 
middle name and/or a last name, like “Arthur C. Clarke”. We could express it 
like this:

	node "contact" {
		node "firstname" { type "string" }
		node "middlename" { type "string" occurs "0..1" }
		node "lastname" { type "string" occurs "0..1" }
	}

At first sight, this suits our requirement. First name is mandatory, so we 
have at least that, and middle and last name are both optional, because not 
everybody  has a middle name, or we simply choose not to supply either one.

However, if you omit the last name, you usually do not supply a middle name 
either. It makes sense to refer to “Arthur”, or “Arthur Clarke”, but “Arthur 
C.” is rather odd (ok, maybe not for a rapper).

It would be nice if we could express that, admittedly, the middle name is 
optional, but should always be followed by a last name. This is where a group 
helps:

	node "contact" {
		node "firstname" { type "string" }
		group {
			occurs "0..1"
			node "middlename" { type "string" occurs "0..1" }
			node "lastname" { type "string" }
		}
	}

Since the group is optional you can omit both the middle and the last name. But 
once there is a middle name, the group is “invoked”, and because the last name  
is mandatory within it, you cannot omit it. You can supply a last name without 
the middle one though, because thaty one is optional within the group.

### The choice is yours

The next tool on your schema belt is a `choice` group, which you can use to 
express that what follows may be described by two (or more) *mutually* *exclusive* 
content models. For instance, consider this:

	node "contact" {
		node "firstname" { type "string" }
		choice {
			node "phonenumber" { type "string" }
			node "emailaddress" { type "string" }
		}
	}

This means that the first name must be followed by either a phone number, or 
an email address, but *not* *by* *both*. In other words, you must choose.

Granted, this is a silly example. Most people have a phone number as well as 
an email address, and - in fact – many people have more than one of each. In 
an attempt to fix that, we make both the phone number and email address 
repeating:

	choice {
		node "phonenumber" { type "string" occurs "1..*" }
		node "emailaddress" { type "string" occurs "1..*" }
	}

Although this allows more than one phone or email record, it does not allow 
both. Instead, we end up with a choice between two different lists. What we 
really needed to do is to change the multiplicity of the choice itself:

	choice {
		occurs "1..*"
		node "phonenumber" { type "string" }
		node "emailaddress" { type "string" }
	}

This says we expect at least one phone number or an email address, but possibly 
more (in which case they may interleave). Although it is a silly example, it does 
illustrate how to create a content model you would not be able to express 
otherwise.

Here is another example, which in addition uses a `group` to combine two nodes 
into one “option”:

	node "location" {
		choice {
			group {
				node "latitude" { type "decimal" }
				node "longitude" { type "decimal" }
			}
			node { type "address" }
		}
	}

In other words, a location is either a set of coordinates, or an address (this is 
assuming we have declared an address type somewhere).

### Order! Order!

When processing data, machines generally do not care about the order in which 
they receive it. By contrast, humans are very particular about order – if not 
for aesthetic reasons, then surely for practical ones.

That is why the order in which SDA nodes are expected, equals the order in 
which you defined them (within their context type).

I have been working in the field of application integration for most of my 
professional life, and I have hardly ever seen a design that allows or expects 
data to be in any order. But I do remember using this pattern as a workaround to 
deal with applications that are unable to enforce to a particular order.

Maybe in other areas, this is more common, I have no idea. But regardless, SDS is 
able to support this. For example, if we need to allow the first and last names 
of a contact to appear in any order, this is how we define them to be `unordered`:

	node "contact" {
		unordered {
			node "firstname" { type "string" }
			node "lastname" { type "string" }
		}
	}

This will accept both

	contact { firstname "Edsger" lastname "Dijkstra" } 			

and

	contact { lastname "Dijkstra" firstname "Edsger" }

This also works for optional, and repetitive types:

	node "contact" {
		unordered {
			node "firstname" { type "string" occurs "1..*" }
			node "lastname" { type "string" occurs "0..1" }
		}
	}

Which will accept the previous two examples, and in addition

	contact { firstname "Edsger" firstname "Wybe" lastname "Dijkstra" }

and

	contact { lastname "Dijkstra" firstname "Edsger" firstname "Wybe" }

and

	contact { firstname "Edsger" firstname "Wybe" }

and

	contact { firstname "Edsger" }

but *not*

	contact { firstname "Edsger" lastname "Dijkstra" firstname "Wybe" }

because that breaks up the repeating 'firstname' node.

And with that, we have reached the end of this chapter, and almost the end of 
this document.


## Conclusion

This is the second release of the SDS specification. Like SDA, it is simple, 
yet powerful, and not going to take over the world any time soon. 

Some useful features are still absent, because I could not make them “fit”, or 
because I wasn't prepared to go there. Yet. 

For now this will have to do.
