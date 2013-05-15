#!/usr/bin/env groovy
import groovy.json.JsonSlurper

// Twitter URL request
def twitterEndpoint='https://search.twitter.com/search.json?q=breizhcamp&rpp=20'
// sleep time
def timeSleep = 2000

// Json Parser
def jsonSlurper = new JsonSlurper();

// Tweet printer
printTweet = {
    sleep(timeSleep)
    println "--"
    println it.from_user_name + " - " + "@"+it.from_user 
    List<String> lines = wrapLine(it.text, 80);
    lines.each {line -> println line}
    //println it.text
    def dateTweet = Date.parse(it.created_at)
    // Format cible : "14/05/2013 11:41:04"
    println "-- " + String.format("%td/%<tm/%<tY %<tT", dateTweet)
}

// Line wrapper
// From : http://groovy.codehaus.org/Formatting+simple+tabular+text+data
List<String> wrapLine(input, lineWidth) {
    List<String>  lines = []
    def           line = ""
    def           addWord;

    addWord = {word ->
      // Add new word if we have space in current line
      if ((line.size() + word.size()) <= lineWidth) {
        line <<= word
        if (line.size() < lineWidth)
          line <<= " "
        // Our word is longer than line width, break it up
      } else if (word.size() > lineWidth) {
        def len = lineWidth - line.length()
        line <<= word.substring(0, len)
        word = word.substring(len)
        lines += line.toString()

        while (word.size() > lineWidth) {
          lines += word.substring(0, lineWidth);
          word = word.substring(lineWidth);
        }
        line = word
        if (line.size() > 0 && line.size() < lineWidth)
          line <<= " "
        // No more space in line - wrap to another line
      } else {
        lines += line.toString()
        line = ""

        addWord(word)
      }
    }

    input.split(" ").each() {
      addWord(it)
    }

    lines += line.toString()

    return lines
  }

// Main loop
while (true) {
    def jsonContent = twitterEndpoint.toURL().text
    def jsonData = jsonSlurper.parseText(jsonContent)
    def tweets = jsonData.results
    tweets.each(printTweet)
}
