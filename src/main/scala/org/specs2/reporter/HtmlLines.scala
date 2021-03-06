package org.specs2
package reporter

import scala.xml._
import text.Plural._
import text._
import form._
import Form._
import execute._
import matcher.DataTable
import specification._
import org.specs2.internal.scalaz.Scalaz._
import Stats._
import main.{Report, Arguments}

/**
 * The HtmlLines groups a list of HtmlLine to print
 * 
 * It can be written ('flushed') to an HtmlResultOuput by printing them one by one to this output
 *
 */
private[specs2]
case class HtmlLines(specName: SpecName, lines : List[HtmlLine] = Nil, link: HtmlLink) {
  def printXml(implicit out: HtmlResultOutput) = lines.foldLeft(out) { (res, cur) => cur.print(res) }
  def add(line: HtmlLine) = copy(lines = lines :+ line)
  def nonEmpty = !isEmpty
  def isEmpty = lines.isEmpty
  
  override def toString = (link +: lines).mkString("\n")
}

/** 
 * An HtmlLine encapsulates:
 * 
 * * an Html fragment to print
 * * the current statistics
 * * the current level
 * * the current arguments
 */
private[specs2]
case class HtmlLine(text: Html = HtmlBr(), stats: Stats = Stats(), level: Int = 0, args: Arguments = Arguments()) {
  def print(implicit out: HtmlResultOutput): HtmlResultOutput = text.print(stats, level, args)
  
  override def toString = text.toString
}

/**
 * This trait represents any kind of Html fragment to print
 * 
 * It has a print method taking an HtmlResultOutput and returning another HtmlResultOutput
 * containing the html code to print
 *
 */
private[specs2]
sealed trait Html {
  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput): HtmlResultOutput
}
private[specs2]
case class HtmlSpecStart(start: ExecutedSpecStart) extends Html {
  def isSeeOnlyLink = start.isSeeOnlyLink
  def isIncludeLink = start.isIncludeLink
  def isLink        = start.isLink
  def link          = start.link
  def unlink        = HtmlSpecStart(start.unlink)

  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput) = {
    if (!args.xonly) {
      start.link.map(l => out.printLink(l, level, stats)(args)).getOrElse(out.printSpecStart(start.specName, stats)(args))
    } else out
  }

  override def toString = start.toString
}
private[specs2]
case class HtmlText(t: ExecutedText) extends Html {
  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput) =
    if (!args.xonly) out.printText(t.text, level, !args.xonly)(args) else out

  override def toString = t.toString
}
private[specs2]
case class HtmlBr() extends Html {
  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput) =
    if (!args.xonly) out.printPar("", !args.xonly)(args) else out
}
private[specs2]
case class HtmlResult(r: ExecutedResult) extends Html {
  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput) = {
    if (!args.xonly || !r.result.isSuccess) {
      r match {
        case ExecutedResult(FormMarkup(form),_,_,_,_) => printFormResult(form)(args, out)
        case _                                        => printResult(r.text(args), level, r.result)(args, out)
      }

    }
    else out
  }
  def printFormResult(form: Form)(implicit args: Arguments, out: HtmlResultOutput): HtmlResultOutput = out.printElem(form.toXml(args))

  def printResult(desc: MarkupString, level: Int, result: Result)(implicit args: Arguments, out: HtmlResultOutput): HtmlResultOutput = {
    val outDesc = printDesc(desc, level, result)

    result match {
      case f: Failure                           => printFailureDetails(level + 1, f)(args, outDesc)
      case e: Error                             => printErrorDetails(level, e)(args, outDesc).printStack(e, level + 1)
      case Success(_)                           => outDesc
      case Skipped(_, _)                        => outDesc.printSkipped(NoMarkup(result.message), level, !args.xonly)
      case Pending(_)                           => outDesc.printPending(NoMarkup(result.message), level, !args.xonly)
      case DecoratedResult(table: DataTable, r) => printDataTable(table, level)(args, outDesc)
    }
  }

  def printDesc(desc: MarkupString, level: Int, result: Result)(implicit args: Arguments, out: HtmlResultOutput): HtmlResultOutput =
    result match {
      case f: Failure                           => out.printFailure(desc, level)
      case e: Error                             => out.printError(desc, level)
      case Success(_)                           => out.printSuccess(desc, level, !args.xonly)
      case Skipped(_, _)                        => out.printSkipped(desc, level)
      case Pending(_)                           => out.printPending(desc, level)
      case DecoratedResult(table: DataTable, r) => printDesc(desc, level, r)
    }

  def printFailureDetails(level: Int, f: Failure)(implicit args: Arguments, out: HtmlResultOutput) =
    if (args.failtrace) out.printCollapsibleExceptionMessage(f, level + 1).
                            printCollapsibleDetailedFailure(f.details, level + 1, args.diffs.show)
    else                out.printExceptionMessage(f, level + 1).
                            printCollapsibleDetailedFailure(f.details, level + 1, args.diffs.show)

  def printErrorDetails(level: Int, f: Result with ResultStackTrace)(implicit args: Arguments, out: HtmlResultOutput) =
    out.printCollapsibleExceptionMessage(f, level + 1)

  def printDataTable(table: DataTable, level: Int = 0)(implicit args: Arguments, out: HtmlResultOutput) = printFormResult(Form(table))(args, out)
   
  override def toString = r.toString
   
}
private[specs2]
case class HtmlSpecEnd(end: ExecutedSpecEnd, endStats: Stats) extends Html {
  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput) = {
    if ((!args.xonly || stats.hasFailuresOrErrors) && stats.hasExpectations && (stats eq endStats))
      printEndStats(stats)(args, out)
    else out
  }

  def printEndStats(stats: Stats)(implicit args: Arguments, out: HtmlResultOutput) = {
    val title = "Total for specification" + (if (end.name.isEmpty) end.name.trim else " "+end.name.trim)
    val classStatus = if (stats.hasIssues) "failure" else "success"
 
    out.printBr().printElem {
      <table class="dataTable">
        <tr><th colSpan="2">{title}</th></tr>
        <tr><td>Finished in</td><td class="info">{stats.time}</td></tr>
        <tr><td>Results</td><td class={classStatus}>{ stats.displayResults(Arguments("nocolor")) }</td></tr>
      </table>
    }
  }
}
private[specs2]
case class HtmlOther(fragment: ExecutedFragment)   extends Html {
  def print(stats: Stats, level: Int, args: Arguments)(implicit out: HtmlResultOutput) = out
}
