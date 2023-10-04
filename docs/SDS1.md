# SDS V1

<pre>
    <b>This is the documentation for SDS version 1, now obsolete.</b>
</pre>

- [Why we need it](/docs/SDS1.md#why-we-need-it)
- [It is all about looks](/docs/SDS1.md#it-is-all-about-looks)
	- [First contact](/docs/SDS1.md#first-contact)
	- [Dolls, salami and blinds](/docs/SDS1.md#dolls-salami-and-blinds)
	- [Finding the root](/docs/SDS1.md#finding-the-root)
	- [More on multiplicity](/docs/SDS1.md#more-on-multiplicity)
- [Typicalities](/docs/SDS1.md#typicalities)
	- [Matters of space](/docs/SDS1.md#matters-of-space)
	- [Facets of restriction](/docs/SDS1.md#facets-of-restriction)
	- [The nothing that is](/docs/SDS1.md#the-nothing-that-is)
	- [Anything goes](/docs/SDS1.md#anything-goes)
- [Model citizens](/docs/SDS1.md#model-citizens)
	- [Group mentality](/docs/SDS1.md#group-mentality)
	- [The choice is yours](/docs/SDS1.md#the-choice-is-yours)
	- [Order! Order!](/docs/SDS1.md#order-order)
- [Conclusion](/docs/SDS1.md#conclusion)

## Why we need it

Why do we need schema? Well, for starters, a schema allows you to *validate* the 
data you receive, before you process it. This enables you to detect and mitigate
 anomalies before they wreak havoc in your precious application, system of 
record,  or business process.

Another reason is that it makes developing data processing software easier 
because schema can show you at design-time exactly what you are working with. In 
fact, schema allows code generators to eliminate a lot of the software “writing” 
part.

And finally, it makes it easy to share information about your data (and how to 
process it) with other people, without having to write lengthy specifications.

This is true regardless of whether the data is “encoded” as EDI (oh, the days of
 yore), XML, JSON, or – in fact - SDA.

## It is all about looks

What does SDA schema (SDS for short) look like? Well, it looks exactly like SDA.
 Because that is what it *is*. This follows the same approach as XML schema (or 
XSD)  which is usually written in XML and JSON schema, which can be written in 
JSON (or YAML, but that is evil).

The good thing about this approach is that you don’t have to learn another 
“language” to read it (or write another parser to process it).

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

In either case, we could use a schema that describes the address book structure.
 In SDS, such a schema could look like this:

	schema {
		node {
			name "addressbook"
			node {
				name "contact" occurs "0..*"
				node { name "firstname" type "string" }
				node { name "phonenumber" type "string" }
			}
		}
	}

Note that the tags (`schema`, `node`, `name`, `occurs`, `type`) are part of the 
SDS vocabulary, and the values (in quotes) define your particular use case. The 
simple nodes are attributes[^1] of the complex ones which are usually referred to as components. Components are type definitions, or model groups (which we will meet later).

[^1]: We use the word attribute only in the context of SDS terminology to 
describe properties of components. You may recall that SDA has no attributes 
(like XML).

In this example, the schema defines an address book as an SDA node named 
'addressbook' that contains another node named 'contact', which in turn has two 
nodes named 'firstname' and 'phonenumber'.

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

And that is all. Well, it's not of course. It never is. But in terms of 
concepts, this is the gist of it. The remainder of this document will re-iterate
  and discuss this, and more, in pain-staking detail (rest assured).

### Dolls, salami and blinds

Have another look at the schema at the top of this page. It defines the data 
structure in a top-down fashion, starting with the root node 'addressbook' and
 recurses into the child nodes until the entire SDA document is specified.

In XML schema, this pattern is called “Russian Doll” after the decorative dolls 
that contain smaller dolls, containing even smaller ones, and so forth.

An alternative way to accomplish the same thing in another way, is to declare 
small “building blocks” that make up your data and refer to these in order to 
build the bigger structure(s) in your document.

In XML schema, this pattern is called “Salami Slice” or “Venetian Blind”, 
depending on whether your building blocks are declared as elements or types. In 
SDS, that distinction is not explicit; a node declared in the main schema 
section is a re-useable type, that can be referred to by other nodes. I will 
liberally use the terms node and type interchangeably.

Applying this pattern to the address book schema, here is what that looks like:

	schema {
		node {
			name "contact"
			node { name "firstname" type "string" }
			node { name "phonenumber" type "string" }
		}
		node {
			name "addressbook"
			node { type "contact" occurs "0..*" }
		}
	}

As you can see, we moved the definition of the contact to the main section and 
made a type reference in the 'addressbook' node. In doing so, we omitted the `name` attribute, but that is fine, it will default to the name of the type.

What we achieved is re-usability of the contact definition. Granted, in this 
example that is not a real issue, but just to make my point, I modified the 
address book so I can store my own contact details in an 'owner' node:

	…
		node {
			name "addressbook"
			node { name "owner" type "contact" }
			node { type "contact" occurs "0..*" }
		}
	…

Using the `name` attribute I set this node apart from regular contacts, and 
since there is only one owner, I omitted the multiplicity to make it singular 
and mandatory. The book then, could look like this:

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

We can take this even further, by moving out simple types to the main 
section as well:

	schema {
		node { name "phonenumber" type "string" }
		node {
			name "contact"
			node { name "firstname" type "string" }
			node { type "phonenumber" }
		}
	…
	}

In general, this has no benefit for a simple type, unless it has been customized
  and re-used in several places. For instance, the phone number could be 
restricted to allow only sensible values, and act as a super-type for say, a 
home number or a mobile number.

### Finding the root

Looking at the “Russian Doll” style schema, it is visually clear that we intend 
'addressbook' to be the root node of the document, since all other nodes are 
*local* to it.

When moving out types to the main schema section, this is no longer apparent, 
because each of them could – at least in theory - act as a root node.

Depending on the number of nodes in the main section that may be a problem or at
 the very least confusing. If you feel that it is imperative to designate the 
root node, you can do so by using the `type` attribute in the schema node:

	schema {
		type "addressbook"
		node {
			name "contact"
			node { name "firstname" type "string" }
			node { name "phonenumber" type "string" }
		}
		node {
			name "addressbook"
			node { type "contact" occurs "0..*" }
		}
	}

It doesn’t really matter where the designated root node is located and whether 
nodes are declared in order of their “appearance” or not.

### More on multiplicity

Here is a list of the most common values that `occurs` will accept:

**1..1** (or just **1**): this is the default, and equivalent to omitting 
multiplicity altogether. It means the node is singular and mandatory – it must 
occur exactly once.

**0..1**: this also indicates a singular node, but its presence is optional. It
 can occur at most once.

**1..\***: indicates mandatory and unbounded presence; the node must occur at least once.

**0..\***: means optional and unbounded. The node may occur any number of times or not at all.

Not often will you come across a node that must occur – say - exactly 2 times,  
or between 4 and 8 times. But in case you have this requirement, the general 
syntax for specifying multiplicity is

**n..m**: where n and m are both non-negative integers and **n** <= **m**,

**n..\***: where \* means unbounded occurrence, or

**n**: which means the node must occur exactly **n** times.

This includes **0** (or **0..0**), which means the node may not occur at all. 
That makes little sense, as you would not declare such a node in the first 
place. You can think of this as a way to “comment out” the node without having 
to remove it from the schema.

One final thing about multiplicity (one which you may feel is trivial) is that 
it is not an intrinsic property of a type but rather one that depends on the 
current context. A contact is not repeatable because you expect to have more 
than one, but because you usually have more than one *in an address book*.

Likewise, there is nothing inherently singular about an address book, as you 
could have many of them by extending the schema like this:

	node {
		name "shelf" node { type "addressbook" occurs "0..*" }
	}


Enough, let’s go on to types!

## Typicalities

Up to now we have only seen a `string` type in our schema definition. Obviously,
 we need more than that to make sure our data is correctly interpreted by 
others. Without further ado, here are the content types supported  by SDS:

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

**boolean**: the well-known truth values type (true or false).

Compared to the myriad of types and sub-types that are supported in XML 
schema, this may seem limited. That is true; both SDA and SDS are a trade-off 
between versatility and simplicity (or necessity). And that approach can be 
limiting in some ways. On the other hand, when was the last time you actually 
used a `gMonthDate` type?

Having said that, you can use certain attributes called *facets* (covered later)
 to restrict any of the built-in types and mimic most of the derived types that 
you feel are conspicuously absent.

But before we go there, I should explain the difference between the so-called 
value space and lexical space.

### Matters of space

The value space is the set of distinct and valid values for a particular 
type. The lexical space is the set of valid *representations* of that value. 
An example will help to clarify this: 42 is a single value from the value 
space of a number. However, it may be represented as “42”, or “42.0”, or as 
“forty-two”, or “the answer to life, the universe and everything”.

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

The lexical space of this type includes about everything that can be 
unambiguously interpreted as a (rational) number. It includes all integers (with
 or without a leading + or - sign or 0’s), all decimals with a dot separating 
the fractional part, as well as scientific E notation.

For example: “42”, “-.27315”, “+0.314159”, “6.022E23”, or “-1.602e-19”.

#### date

This is a simple type representing a Gregorian calendar date, intended for 
practical use. Its lexical space is the familiar “YYYY-MM-DD” notation. It 
supports neither negative dates (B.C.) nor dates beyond the year 9999. Also, it 
lacks a time zone, which – without knowing the time - is of limited use anyway.

For example, when I tell you I am born on “1968-02-28”, it goes without 
saying that for some people on this planet it may have been the 
27<sup>th</sup>, or the 29<sup>th</sup> of February depending on the time I 
was born, which – by the way – I do not know. Adding a time zone hardly 
narrows it down, much like my father always says he was “at the pub” when my 
mother gave birth.

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
or “false” in lowercase, and “0” or “1” are not accepted either!

### Facets of restriction

Facets are special attributes to restrict the built-in types and create 
sub-types as it were. The imperative word here is *restrict*; there is no way 
you can extend the value or lexical space of any type using a facet. Here are 
the facets at your disposal:

#### length

This facet applies to string and binary types. The restriction on length is 
specified in the exact same syntax we encountered when talking about 
multiplicity. For a string type, length is the number of characters, whereas for
 binary types it is the number of *bytes*. Here are a few examples:

	node { name "char3" type "string" length "3" }

	node { name "nonEmptyString" type "string" length "1..*" }

	node { name "imageMax1MB" type "binary" length "0..1048576" }

Omitting the length facet is equivalent to **0..\***.

#### value

The value facet restricts the value space of integer, decimal and date(time) 
types. The syntax is similar to that for length and multiplicity, except that  
standard interval notation is used, unless a single value is supplied. Limits 
can be inclusive or exclusive depending on whether brackets (inclusive) or 
parentheses (exclusive) are used:

	node { name "negativeInteger" type "integer" value "(*..0)" }

	node { name "signedByte" type "integer" value "[-128..128]" }

	node { name "temperature" type "decimal" value "[-273.15..*)" }

	node { name "PI" type "decimal" value "3.1415926535" }

	node { name "today" type "date" value "2020-08-12" }

	node { name "thisYear" type "date" value "[2020-01-01..2021-01-01)" }

	node { name "now" type "datetime" value "2020-08-12T11:46:00+02:00" }

	node {
		name "today" type "datetime"
		value "[2020-08-12T00:00:00+02:00..2020-08-13T00:00:00+02:00)"
	}

Omitting the value facet is equivalent to **(\*..\*)**.

#### pattern

The final facet is different from length and value in two ways: first, it  
works on all simple types, and second, it restricts the lexical space rather 
than the value space. A pattern lets you derive all kinds of custom types 
using the power of a regular expression. The possibilities are literally 
endless, so here are just a few to whet your appetite:

	node { name "token" type "string" pattern "[^\\s]" }

	node { name "time24" type "string" pattern "([01][0-9]|2[0-3]):[0-5][0-9]" }

	node { name "colour" type "string" pattern "red|yellow|blue" }

In the first example we need to escape the backslash because it is the SDA 
escape character. The last example illustrates how to create an enumeration 
using a pattern.

### The nothing that is

This is a perfect opportunity for me to rant about empty nodes and why you 
should try to avoid them. Oh wait, I already did that years ago. Please refer 
to  the SDA specification, in particular the sections about empty nodes and 
the lack of explicit nil.

So, while there is nothing “illegal” about empty nodes in SDA, you should not 
get in the habit of creating them if it can be avoided. Not merely because it 
is  a waste of bandwidth, but also because funny things can happen when they 
are processed by non-suspecting applications.

Schema validation will help you catch emptiness when it is potentially 
dangerous. For example, if somewhere in your schema you have this:

	…
	node { name "age" type "integer" occurs "0..1" }
	…

And the data you are validating against that schema looks like this:

	…
	personal { age "" gender "male" }
	…

then validation would (should) reject the empty 'age' node. Why? Because 
“nothing” is not an integer. Nor is it a date, or a Boolean, or anything 
other than an empty string (which is fine for a `string` type).

In this case, since age was declared optional, the right  thing to do would 
be to omit it altogether. So rather than empty, it would be absent, and 
therefor unknown.

But what if age had been mandatory, or if you truly wanted (needed) to 
transmit an empty value? Maybe the empty node could not be avoided due to a 
technical restriction. Or perhaps you wanted the recipient to explicitly 
clear (nullify) the age record in a database.

In that case you could define age as a `string` – which would work fine but
 also defeat the purpose of data typing and validation. A better solution 
is to allow it to be empty (or null[^2]):

	node { name "age" type "integer" nullable "true" }

Presto, you have just allowed age to be empty. However, I feel obliged to 
point out that you should use this with care (so not unless there is a good 
reason for it).

Now, about strings. These can be empty practically by definition, but if that 
is  not what you want, you can use the length or pattern facet to require at 
least one character. Or alternatively you just say:

	node { name "id" type "string" nullable "false" }

[^2]: There are really interesting discussions on the Internet whether it 
should  be nil or null, and what that value actually represents. I decided to 
run with null and nullable, rather than nil and nillable.

### Anything goes

Before we leave the subject of types and restrictions, there is one more 
special  “type” we need to discuss. So far, schema has enabled us to specify 
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

	node {
		name "contact" occurs "0..*"
		node { name "firstname" type "string" }
		node { name "phonenumber" type "string" }
		node {
			name "about" occurs "0..1"
			node { type "any" occurs "1..*" }
		}
	}

This specifies that the phone number is optionally followed by an 'about' 
node, containing one or more nodes with any name, of any type. Note that this 
is slightly different from:

	…
	node { name "phonenumber" type "string" }
	node { name "about" type "any" occurs "0..1" }
	…

because when specified like that, the node may be of simple content, like so:

	contact {
		firstname "Bob" phonenumber "06-90784523"
		about "I like to fish and read books."
	}


It is never a bad idea to confine “undefined” content to a (complex) node 
with a known name, like in the examples above. But it does not need to 
be. When  the contact is defined like this:

	node {
		name "contact" occurs "0..*"
		node { name "firstname" type "string" }
		node { name "phonenumber" type "string" }
		node { type "any" occurs "0..*" }
	}

then any number of nodes, of any name and any type may follow the phone 
number (which is probably not the best design choice). Also, these wildcard 
nodes should preferably come after the regular types, because the validation 
process may not be able to disambiguate the content otherwise.

## Model citizens

So far, we have seen only one way to build a content model; by defining nodes 
within other nodes and making them optional or mandatory, and possibly 
repetitive. Although this will get you a long way, some patterns are cumbersome 
or even impossible to express without more advanced features. These *model* 
*groups* will be the subject of this chapter.

### Group mentality

First, there is the (sequence) `group`, which you use to group nodes that have an interdependence. A (somewhat contrived) example will explain this better.

Assume that we want to model a contact with at least a first name, maybe a 
middle name and/or a last name, like “Arthur C. Clarke”. We could express it 
like this:

	node {
		name "contact"
		node { name "firstname" type "string" }
		node { name "middlename" type "string" occurs "0..1" }
		node { name "lastname" type "string" occurs "0..1" }
	}

At first sight, this suits our requirement. First name is mandatory, so we 
have at least that, and middle and last name are both optional, because not 
everybody  has a middle name, or we simply choose not to supply either one.

However, if you omit the last name, you usually do not supply a middle name 
either. It makes sense to refer to “Arthur”, or “Arthur Clarke”, but “Arthur 
C.” is rather odd (ok, maybe not for a rapper).

It would be nice if we could express that, admittedly, the middle name is 
optional, but should always be followed by a last name. This is where a group 
node helps:

	node {
		name "contact"
		node { name "firstname" type "string" }
		group {
			occurs "0..1"
			node { name "middlename" type "string" occurs "0..1" }
			node { name "lastname" type "string" }
		}
	}

Since the group is optional you can omit both the middle and the last name. But 
once there is a middle name, the group is “invoked”, and because the last name  
is mandatory within it, you cannot omit it. You can supply a last name without 
the middle one though, because the latter is optional within the group.

### The choice is yours

The next tool on your schema belt is a `choice` group, which you can use to 
express that what follows may be described by two (or more) *mutually* *exclusive* content models. For instance, consider this:

	node {
		name "contact"
		node { name "firstname" type "string" }
		choice {
			node { name "phonenumber" type "string" }
			node { name "emailaddress" type "string" }
		}
	}

This means that the first name must be followed by either a phone number, or 
an email address, but *not* *by* *both*. In other words, you must choose.

Granted, this is a silly example. Most people have a phone number as well as 
an email address, and - in fact – many people have more than one of each. In 
an attempt to fix that, we make both the phone number and email address 
repeating:

	choice {
		node { name "phonenumber" type "string" occurs "1..*" }
		node { name "emailaddress" type "string" occurs "1..*" }
	}

Although this allows more than one phone or email record, it does not allow 
both. Instead, we end up with a choice between two different lists. What we 
really needed to do is to change the multiplicity of the choice itself, like so:

	choice {
		occurs "1..*"
		node { name "phonenumber" type "string" }
		node { name "emailaddress" type "string" }
	}

This says we expect at least one occurrence of a phone number (or an email 
address), and possibly more (in which case they may interleave). This example is
  a bit contrived, but it does illustrate how to create a content model you 
would  not be able to express otherwise.

Here is another example, which in addition uses a `group` to combine two nodes into one “option”:

	node {
		name "location"
		choice {
			group {
				node { name "latitude" type "decimal" }
				node { name "longitude" type "decimal" }
			}
			node { type "address" }
		}
	}

In other words, a location is either a set of coordinates, or an address 
(this is assuming we have declared an address type somewhere).

### Order! Order!

When processing data, machines generally do not care about the order in which 
they receive it. By contrast, humans are very particular about order – if not 
for aesthetic reasons, then surely for practical ones.

That is why the order in which SDA nodes are expected, equals the order in 
which  you defined them in the schema. So, there is no need for an explicit 
sequence, like there is in XML schema; in SDS that is implied, which I 
believe is a sensible choice.

I have been working in the field of application integration for the bigger 
part of my professional life, and I have hardly ever seen a design that 
allows or expects data to be in any order. But I do remember using this 
pattern as a workaround to deal with applications that are unable to enforce 
to a particular order.

Maybe in other areas, this is more common, I have no idea. But even if it is 
not, SDS is able to support this. For example, if we need to allow the first 
and last names of a contact to appear in any order, this is how we define 
them to be `unordered`:

	node {
		name "contact"
		unordered {
			node { name "firstname" type "string" }
			node { name "lastname" type "string" }
		}
	}

This will accept both

	contact { firstname "Edsger" lastname "Dijkstra" } 			

and

	contact { lastname "Dijkstra" firstname "Edsger" }

This also works for optional, and repetitive nodes:

	…
	unordered {
		node { name "firstname" type "string" occurs "1..*" }
		node { name "lastname" type "string" occurs "0..1" }
	}
	…

Which will accept the previous two examples, and also

	contact { firstname "Edsger" firstname "Wybe" lastname "Dijkstra" }

and

	contact { lastname "Dijkstra" firstname "Edsger" firstname "Wybe" }

and

	contact { firstname "Edsger" firstname "Wybe" }

and

	contact { firstname "Edsger" }

but *not*

	contact { firstname "Edsger" lastname "Dijkstra" firstname "Wybe" }

because that breaks up the repeating firstname.

And with that, we have reached the end of this chapter, and almost the end of 
this document.

## Conclusion

This is it, the first draft of the SDS specification. Like with SDA,  it is not 
going to take over the world any time soon. Compared to XSD or Relax NG, a lot is 
missing, but that was the point; to keep things simple and yet powerful. 

Some useful features I excluded because I could not make them “fit” right away, or 
because they would bring up the question of namespace support. On the other hand, 
some features I included though I wasn't sure they would be necessary.

This may or may not be the end of it. Time will tell.
