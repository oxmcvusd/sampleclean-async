package sampleclean.clean.deduplication.join

import sampleclean.api.SampleCleanContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import sampleclean.clean.deduplication.matcher.Matcher

/**
 * This class acts as a wrapper for blocker + matcher routines:
 * simjoin + List[Matchers]. We treat a similarity join
 * as a combination blocking and matching sequence.
 *
 * We call this the "BlockerMatcherJoinSequence" because
 * in this class we join two different datasets (with the same
 * schema).
 * 
 * @type {[type]}
 */
class BlockerMatcherJoinSequence(scc: SampleCleanContext,
              		   sampleTableName:String,
              		   simjoin:SimilarityJoin,
					   matchers: List[Matcher]) extends Serializable {

	//def this(scc: SampleCleanContext,
    //          		   sampleTableName:String,
    //          		   simjoin: SimilarityJoin = null,
	//				   matchers: List[Matcher] = List())

  /**
   * Execute the algorithm.
   */
	def blockAndMatch(data1:RDD[Row], data2:RDD[Row]):RDD[(Row,Row)] = {

		var matchedData = simjoin.join(data1,data2,true,true)

		for (m <- matchers)
		{
			matchedData = m.matchPairs(matchedData)
		}

		return matchedData
	}

	private [sampleclean] def updateContext(newContext:List[String]) = {

		if (simjoin != null)
			simjoin.updateContext(newContext)

		for (m <- matchers)
			m.updateContext(newContext)
		
	}

}

