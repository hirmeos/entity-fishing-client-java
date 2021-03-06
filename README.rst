Entity Fishing JAVA client
==========================

.. image:: http://img.shields.io/:license-apache-blue.svg
    :target: http://www.apache.org/licenses/LICENSE-2.0.html

.. image:: https://travis-ci.org/hirmeos/entity-fishing-client-java.svg?branch=master
    :target: https://travis-ci.org/hirmeos/entity-fishing-client-java


Java client to query the `Entity Fishing service API`_ developed in the context of the EU H2020 HIRMEOS project (WP3).
For more information about entity-fishing, please check the `Entity Fishing Documentation`_. 

.. _Entity Fishing service API: http://github.com/kermitt2/nerd
.. _Entity Fishing Documentation: http://nerd.readthedocs.io


(Work in progress)


Installation
------------

The latest version is 0.0.2.

You need to add the dependency in your `gradle.build` file:

.. code-block::

    repositories { maven { url "https://dl.bintray.com/rookies/maven" } }


and add the dependency:

.. code-block::

    compile 'com.scienceminer.nerd:entity-fishing-client:0.0.2'


or your `pom.xml`:

.. code-block:: xml

    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-rookies-maven</id>
            <name>bintray</name>
            <url>https://dl.bintray.com/rookies/maven</url>
        </repository>
    </repositories>

and add the dependency:

.. code-block:: xml

    <dependency>
      <groupId>com.scienceminer.nerd</groupId>
      <artifactId>entity-fishing-client</artifactId>
      <version>0.0.2</version>
      <type>pom</type>
    </dependency>


To build from the sources:

    > git clone

    > ./gradlew clean install

Usage
-----

Disambiguation
##############

.. code-block:: java 

    NerdClient client = new NerdClient()

To disambiguate text (> 5 words):

.. code-block:: java

    client.disambiguateText("Linux is a name that broadly denotes a family of free and open-source software operating systems (OS) built around the Linux kernel.", "en")

To disambiguate a search query

.. code-block:: java

    client.disambiguateQuery("python method acronym concrete")

To process a PDF:

.. code-block:: java

    client.disambiguatePDF(pdf, language)

you can supply the language (iso form of two digits, en, fr, etc..) and the entities (only for text).

The response is a ObjectNode, representing a json object:

.. code-block::

    {
        'entities': [
            {
                'domains': ['Computer_Science'],
                'nerd_score': 0.3753,
                'nerd_selection_score': 0.7268,
                'offsetEnd': 5,
                'offsetStart': 0,
                'rawName': 'Linux',
                'type': 'PERSON',
                'wikidataId': 'Q388',
                'wikipediaExternalRef': 6097297
            },
            {
                'domains': ['Computer_Science'],
                'nerd_score': 0.7442,
                'nerd_selection_score': 0.85,
                'offsetEnd': 78,
                'offsetStart': 49,
                'rawName': 'free and open-source software',
                'wikidataId': 'Q506883',
                'wikipediaExternalRef': 1721496
            },
            {
                'domains': ['Electrotechnology', 'Electronics',
                'Computer_Science'],
                'nerd_score': 0.7442,
                'nerd_selection_score': 0.4487,
                'offsetEnd': 96,
                'offsetStart': 79,
                'rawName': 'operating systems',
                'wikidataId': 'Q9135',
                'wikipediaExternalRef': 22194
            },
            {
                'domains': [
                    'Electrotechnology', 'Electronics', 'Computer_Science'
                ],
                'nerd_score': 0.7442,
                'nerd_selection_score': 0.4487,
                'offsetEnd': 100,
                'offsetStart': 98,
                'rawName': 'operating systems',
                'wikidataId': 'Q9135',
                'wikipediaExternalRef': 22194
            },
            {
                'domains': ['Electronics', 'Computer_Science'],
                'nerd_score': 0.743,
                'nerd_selection_score': 0.8383,
                'offsetEnd': 131,
                'offsetStart': 119,
                'rawName': 'Linux kernel',
                'wikidataId': 'Q14579',
                'wikipediaExternalRef': 21347315
            }
        ],
        'global_categories': [
            {'category': 'Finnish inventions',
            'page_id': 27421536,
            'source': 'wikipedia-en',
            'weight': 0.09684039970133569},
           {'category': 'Free software programmed in C',
            'page_id': 11241711,
            'source': 'wikipedia-en',
            'weight': 0.06433942787438053},
           {'category': 'Unix variants',
            'page_id': 10429397,
            'source': 'wikipedia-en',
            'weight': 0.09684039970133569},
           {'category': 'Operating systems',
            'page_id': 693664,
            'source': 'wikipedia-en',
            'weight': 0.12888888710813473},
           {'category': 'Free software',
            'page_id': 693287,
            'source': 'wikipedia-en',
            'weight': 0.06444444355406737},
           {'category': 'Free system software',
            'page_id': 6721544,
            'source': 'wikipedia-en',
            'weight': 0.06433942787438053},
           {'category': 'Software licenses',
            'page_id': 703100,
            'source': 'wikipedia-en',
            'weight': 0.06444444355406737},
           {'category': 'Linux kernel',
            'page_id': 13215678,
            'source': 'wikipedia-en',
            'weight': 0.06433942787438053},
           {'category': 'Monolithic kernels',
            'page_id': 10730969,
            'source': 'wikipedia-en',
            'weight': 0.06433942787438053},
           {'category': '1991 software',
            'page_id': 11167446,
            'source': 'wikipedia-en',
            'weight': 0.09684039970133569},
           {'category': 'Linus Torvalds',
            'page_id': 53479567,
            'source': 'wikipedia-en',
            'weight': 0.09684039970133569}
        ],
        'language': {'conf': 0.9999973266294648, 'lang': 'en'},
        'nbest': False,
        'onlyNER': False,
        'runtime': 107,
        'sentences': [{'offsetEnd': 132, 'offsetStart': 0}],
        'text': 'Linux is a name that broadly denotes a family of free and open-source software operating systems (OS) built around the Linux kernel.'
    }



KB access
#########
.. code-block:: java

   nerd.getConcept("Q456")


The response is using ObjectNode, modelling a json object in the form as follow:

.. code-block::

    {
        'rawName': 'Lyon',
        'preferredTerm': 'Lyon',
        'nerd_score': 0,
        'nerd_selection_score': 0,
        'wikipediaExternalRef': 8638634,
        'wikidataId': 'Q456',
        'definitions': [
          {
            'definition': "'''Lyon''' ( or ;, locally: ; ), also known as ''Lyons'', is a city in east-central [[France]], in the [[Auvergne-Rhône-Alpes]] [[Regions of France|region]], about from [[Paris]], from [[Marseille]] and from [[Saint-Étienne]]. Inhabitants of the city are called ''Lyonnais''.",
            'source': 'wikipedia-en',
            'lang': 'en'
          }
        ],
        'domains': [
          'Geology',
          'Sociology'
        ],
        'categories': [
          {
            'source': 'wikipedia-en',
            'category': 'World Heritage Sites in France',
            'page_id': 1178961
          },
          [...]
        ],
        'multilingual': [
          {
            'lang': 'de',
            'term': 'Lyon',
            'page_id': 13964
          },
          {
            'lang': 'es',
            'term': 'Lyon',
            'page_id': 46490
          },
          {
            'lang': 'fr',
            'term': 'Lyon',
            'page_id': 802627
          },
          {
            'lang': 'it',
            'term': 'Lione',
            'page_id': 41786
          }
        ],
        'statements': [
          {
            'conceptId': 'Q456',
            'propertyId': 'P1082',
            'propertyName': 'population',
            'valueType': 'quantity',
            'value': {
              'amount': '+500716',
              'unit': '1',
              'upperBound': '+500717',
              'lowerBound': '+500715'
            }
          },
          {
            'conceptId': 'Q456',
            'propertyId': 'P1082',
            'propertyName': 'population',
            'valueType': 'quantity',
            'value': {
              'amount': '+500716',
              'unit': '1',
              'upperBound': '+500717',
              'lowerBound': '+500715'
            }
          },
          {
            'conceptId': 'Q456',
            'propertyId': 'P1464',
            'propertyName': 'category for people born here',
            'valueType': 'wikibase-item',
            'value': 'Q8061504'
          },
          {
            'conceptId': 'Q456',
            'propertyId': 'P190',
            'propertyName': 'sister city',
            'valueType': 'wikibase-item',
            'value': 'Q5687',
            'valueName': 'Jericho'
          },
          {
            'conceptId': 'Q456',
            'propertyId': 'P190',
            'propertyName': 'sister city',
            'valueType': 'wikibase-item',
            'value': 'Q2079',
            'valueName': 'Leipzig'
          },
          {
            'conceptId': 'Q456',
            'propertyId': 'P190',
            'propertyName': 'sister city',
            'valueType': 'wikibase-item',
            'value': 'Q580',
            'valueName': 'Łódź'
          },
          [...]
        ]
    }

Utilities
#########

Language detection
==================
.. code-block:: java

   client.getLanguage("This is a sentence. This is a second sentence.")


with response

.. code-block:: python

   (
      {
         'sentences':
         [
            {'offsetStart': 0, 'offsetEnd': 19},
            {'offsetStart': 19, 'offsetEnd': 46}
         ]
      },
      200
   )

Segmentation
============
.. code-block:: python

   client.segment("This is a sentence. This is a second sentence.")


with response

.. code-block:: python

    (
        {
            "lang": "en",
            "conf": 0.9
        },
        200
    )



Multithread client
==================
The multithread client can be used to process a directory of PDFs and save the results into another directory.


Usage
-----

Parameters:

* ``-in``: path to the directory containing the PDF files to process
* ``-out``: path to the directory where to put the results
* ``-n``: concurrency for service usage (default 10)

For example:

    > java -jar target/org.grobid.client-0.5.2-SNAPSHOT.one-jar.jar -in ~/tmp/in2 -out ~/tmp/out

This command will process all the PDF files present in the input directory (files with extension ``.pdf`` only) using the default ``10`` concurrent workers.

    > java -jar target/org.grobid.client-0.5.2-SNAPSHOT.one-jar.jar -in ~/tmp/in2 -out ~/tmp/out -n 20

This command will process all the PDF files present in the input directory (files with extension ``.pdf`` only) and write the resulting JSON files under the output directory, reusing the file name with a different file extension (``.json``), using ``20`` concurrent workers.


