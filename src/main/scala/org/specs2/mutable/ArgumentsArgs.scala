package org.specs2
package mutable

import main._
import text._
import specification.Fragments
import control.{StackTraceFilter, Property}
import reporter.Colors

/**
 * This trait provides shortcuts to create Arguments instances and adding them to the SpecificationStructure by mutating its
 * current content
 */
trait ArgumentsArgs extends main.ArgumentsArgs { this: FragmentsBuilder =>
  override lazy val args = new ArgumentsNamespaceMutable

  /** shorthand method to create an Arguments object */
  override def args(
    ex:            ArgProperty[String]            = ArgProperty[String](),
    include:       ArgProperty[String]            = ArgProperty[String](),
    exclude:       ArgProperty[String]            = ArgProperty[String](),
    wasIssue:      ArgProperty[Boolean]           = ArgProperty[Boolean](),
    was:           ArgProperty[String]            = ArgProperty[String](),
    plan:          ArgProperty[Boolean]           = ArgProperty[Boolean](),
    skipAll:       ArgProperty[Boolean]           = ArgProperty[Boolean](),
    stopOnFail:    ArgProperty[Boolean]           = ArgProperty[Boolean](),
    sequential:    ArgProperty[Boolean]           = ArgProperty[Boolean](),
    xonly:         ArgProperty[Boolean]           = ArgProperty[Boolean](),
    showOnly:    ArgProperty[String]            = ArgProperty[String](),
    color:         ArgProperty[Boolean]           = ArgProperty[Boolean](),
    noindent:      ArgProperty[Boolean]           = ArgProperty[Boolean](),
    markdown:      ArgProperty[Boolean]           = ArgProperty[Boolean]()) =

    addArguments(super.args(
      ex,
      include,
      exclude,
      wasIssue,
      was,
      plan,
      skipAll,
      stopOnFail,
      sequential,
      xonly,
      showOnly,
      color,
      noindent,
      markdown))


  private[specs2] class ArgumentsNamespaceMutable extends ArgumentsNamespace{
    /** shorthand method to create an Arguments object */
    override def select(
      ex:            ArgProperty[String]            = ArgProperty[String](),
      include:       ArgProperty[String]            = ArgProperty[String](),
      exclude:       ArgProperty[String]            = ArgProperty[String](),
      wasIssue:      ArgProperty[Boolean]           = ArgProperty[Boolean](),
      was:           ArgProperty[String]            = ArgProperty[String](),
      specName:      ArgProperty[String]            = ArgProperty[String]()) = addArguments(super.select(
        ex,
        include,
        exclude,
        wasIssue,
        was,
        specName))

    /** shorthand method to create an Arguments object */
    override def execute(
      plan:          ArgProperty[Boolean]           = ArgProperty[Boolean](),
      skipAll:       ArgProperty[Boolean]           = ArgProperty[Boolean](),
      stopOnFail:    ArgProperty[Boolean]           = ArgProperty[Boolean](),
      sequential:    ArgProperty[Boolean]           = ArgProperty[Boolean](),
      threadsNb:     ArgProperty[Int]               = ArgProperty[Int]()
    ) = addArguments(super.execute(
        plan,
        skipAll,
        stopOnFail,
        sequential,
        threadsNb))

    /** shorthand method to create an Arguments object */
    override def store(
      reset:       ArgProperty[Boolean]           = ArgProperty[Boolean](),
      never:       ArgProperty[Boolean]           = ArgProperty[Boolean]()
    ) = addArguments(super.store(
                     reset,
                     never))

    /** shorthand method to create an Arguments object */
    override def report(
      xonly:        ArgProperty[Boolean]            = ArgProperty[Boolean](),
      showOnly:    ArgProperty[String]            = ArgProperty[String](),
      failtrace:     ArgProperty[Boolean]           = ArgProperty[Boolean](),
      color:         ArgProperty[Boolean]           = ArgProperty[Boolean](),
      colors:        ArgProperty[Colors]            = ArgProperty[Colors](),
      noindent:      ArgProperty[Boolean]           = ArgProperty[Boolean](),
      showtimes:     ArgProperty[Boolean]           = ArgProperty[Boolean](),
      offset:        ArgProperty[Int]               = ArgProperty[Int](),
      markdown:      ArgProperty[Boolean]           = ArgProperty[Boolean](),
      debugMarkdown: ArgProperty[Boolean]           = ArgProperty[Boolean](),
      diffs:         ArgProperty[Diffs]             = ArgProperty[Diffs](),
      fromSource:    ArgProperty[Boolean]           = ArgProperty[Boolean](),
      traceFilter:   ArgProperty[StackTraceFilter]  = ArgProperty[StackTraceFilter]()) = addArguments(super.report(
        xonly,
        showOnly,
        failtrace,
        color,
        colors,
        noindent,
        showtimes,
        offset,
        markdown,
        debugMarkdown,
        diffs,
        fromSource,
        traceFilter))
  }

}
