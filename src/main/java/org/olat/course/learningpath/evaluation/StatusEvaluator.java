/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.course.learningpath.evaluation;

import java.util.Date;
import java.util.List;

import org.olat.course.learningpath.LearningPathStatus;
import org.olat.course.learningpath.ui.LearningPathTreeNode;
import org.olat.course.run.scoring.AssessmentEvaluation;

/**
 * 
 * Initial date: 27 Aug 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public interface StatusEvaluator {
	
	public boolean isStatusDependingOnPreviousNode();
	
	public Result getStatus(LearningPathTreeNode previousNode, AssessmentEvaluation assessmentEvaluation);
	
	public boolean isStatusDependingOnChildNodes();
	
	public Result getStatus(LearningPathTreeNode currentNode, List<LearningPathTreeNode>children);
	
	public static Result result(LearningPathStatus status, Date doneDate) {
		return new StatusEvaluatorResult(status, doneDate);
	}
	
	public interface Result {
		
		public LearningPathStatus getStatus();
		
		public Date getDoneDate();
		
	}

}
