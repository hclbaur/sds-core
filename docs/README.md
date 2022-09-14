# Rationale

After writing the SDA specification in 2008, I spent some time implementing it in 
Java, as a general-purpose library. In the next couple of years, I occasionally 
returned to the subject, until ~~I lost interest~~ other interests found me.

Then in 2020[^1] (woah, was that time whizzing past?) I rediscovered the zip files 
I kept, started toying around with the sources, created a git project and polished 
everything up a little.

[^1]: this was around the time the COVID-19 pandemic hit. Thanks to the lockdowns I 
got to spent quite some free evenings on SDS. 

Not long after I was close to something that could be used to parse, render and 
manipulate data, so I tagged it SDA 1.3.0 - which is rather silly because no one 
was using it - and started thinking about the future.

Which, naturally, would have to include Structured (Simple) Data Schema.

**Harold C.L. Baur, April 2020**

---

# Rationale 2

After changing the SDA syntax to allow nodes to have both simple *and* complex 
content, I obviously needed to change the SDS core library to support validation 
of such nodes. 

This would have been possible without changing the SDS syntax, but I could not 
resist the urge to apply the SDA 2 syntax to SDS, and make it visually more 
intuitive, and easier to read.

Unfortunately that broke backwards compatibility, but I felt this was a small 
price to pay, given that the entire user base of SDS comprised just me.

And besides, this is what semantic versioning is for.

**Harold C.L. Baur, September 2022**

