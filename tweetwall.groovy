#!/usr/bin/env groovy
import groovy.json.JsonSlurper
import java.text.Normalizer

// Twitter URL request
def twitterEndpoint='https://search.twitter.com/search.json?q=breizhcamp&rpp=20'
// sleep time
def timeSleep = 2000

// Json Parser
def jsonSlurper = new JsonSlurper()

// Tweet printer
printTweet = {
    sleep(timeSleep)
    println '--'
    println "${removeAccent(it.from_user_name)} - @${it.from_user }"
    wrapLine(removeAccent(it.text), 80).each { println it }    
    def dateTweet = Date.parse(it.created_at)
    // Format cible : "14/05/2013 11:41:04"
    println "-- ${String.format('%td/%<tm/%<tY %<tT', dateTweet)}"
}

// Workaround pour supprimer les accents sur le minitel (encoding particulier du minitel)
String removeAccent(text) {
    // Thanks to @glaforge : http://glaforge.appspot.com/article/how-to-remove-accents-from-a-string
    Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
}

// Line wrapper
// From : http://groovy.codehaus.org/Formatting+simple+tabular+text+data
List<String> wrapLine(input, lineWidth) {
    def lines = []
    def line = ""
    def addWord

    addWord = { word ->
      // Add new word if we have space in current line
      if ((line.size() + word.size()) <= lineWidth) {
        line <<= word
        if (line.size() < lineWidth)
          line <<= " "
        // Our word is longer than line width, break it up
      } else if (word.size() > lineWidth) {
        def len = lineWidth - line.length()
        line <<= word[0..len]
        word = word.substring(len)
        lines << line

        while (word.size() > lineWidth) {
          lines += word[0..lineWidth]
          word = word.substring(lineWidth)
        }
        line = word
        if (line.size() > 0 && line.size() < lineWidth)
          line <<= " "
        // No more space in line - wrap to another line
      } else {
        lines << line
        line = ""

        addWord(word)
      }
    }

    input.split(" ").each {
      addWord(it)
    }

    lines << line

    lines
  }

// Main loop
while (true) {
    def jsonContent = twitterEndpoint.toURL().text
    def jsonData = jsonSlurper.parseText(jsonContent)
    def tweets = jsonData.results
    tweets.each(printTweet)
}
