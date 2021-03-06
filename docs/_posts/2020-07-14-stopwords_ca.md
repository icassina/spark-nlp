---
layout: model
title: Stop Words Cleaner for Catalan
author: John Snow Labs
name: stopwords_ca
date: 2020-07-14 19:03:00 +0800
tags: [stopwords, ca]
article_header:
  type: cover
use_language_switcher: "Python-Scala-Java"
---

## Description
This model removes 'stop words' from text. Stop words are words so common that they can removed without significantly altering the meaning of a text. Removing stop words is useful when one wants to deal with only the most semantically important words in a text, and ignore words that are rarely semantically relevant, such as articles and prepositions.

{:.btn-box}
<button class="button button-orange" disabled>Live Demo</button>
[Open in Colab](https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/b2eb08610dd49d5b15077cc499a94b4ec1e8b861/jupyter/annotation/english/stop-words/StopWordsCleaner.ipynb){:.button.button-orange.button-orange-trans.co.button-icon}
[Download](https://s3.amazonaws.com/auxdata.johnsnowlabs.com/public/models/stopwords_ca_ca_2.5.4_2.4_1594742440888.zip){:.button.button-orange.button-orange-trans.arr.button-icon}

## How to use

<div class="tabs-box" markdown="1">

{% include programmingLanguageSelectScalaPython.html %}

```python

stop_words = StopWordsCleaner.pretrained("stopwords_ca", "ca") \
        .setInputCols(["token"]) \
        .setOutputCol("cleanTokens")
nlp_pipeline = Pipeline(stages=[document_assembler, tokenizer, stop_words])
light_pipeline = LightPipeline(nlp_pipeline.fit(spark.createDataFrame([['']]).toDF("text")))
results = light_pipeline.fullAnnotate("A part de ser el rei del nord, John Snow és un metge anglès i líder en el desenvolupament de l'anestèsia i la higiene mèdica.")
```

```scala

val stopWords = StopWordsCleaner.pretrained("stopwords_ca", "ca")
        .setInputCols(Array("token"))
        .setOutputCol("cleanTokens")
```

{:.h2_title}
## Results

```bash
[Row(annotatorType='token', begin=2, end=5, result='part', metadata={'sentence': '0'}, embeddings=[]),
Row(annotatorType='token', begin=10, end=12, result='ser', metadata={'sentence': '0'}, embeddings=[]),
Row(annotatorType='token', begin=17, end=19, result='rei', metadata={'sentence': '0'}, embeddings=[]),
Row(annotatorType='token', begin=25, end=28, result='nord', metadata={'sentence': '0'}, embeddings=[]),
Row(annotatorType='token', begin=29, end=29, result=',', metadata={'sentence': '0'}, embeddings=[]),
...]
```

{:.model-param}
## Model Information

{:.table-model}
|---|---|
|Model Name:|stopwords_ca|
|Type:|stopwords|
|Compatibility:|Spark NLP 2.5.4+|
|Edition:|Official|
|Input Labels:|[token]|
|Output Labels:|[cleanTokens]|
|Language:|ca|
|Case sensitive:|false|
|License:|Open Source|

{:.h2_title}
## Data Source
The model is imported from [https://github.com/WorldBrain/remove-stopwords](https://github.com/WorldBrain/remove-stopwords)