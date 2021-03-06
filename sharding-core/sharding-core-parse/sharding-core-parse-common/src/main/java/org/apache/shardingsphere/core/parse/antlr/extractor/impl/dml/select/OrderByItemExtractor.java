/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.parse.antlr.extractor.impl.dml.select;

import com.google.common.base.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.shardingsphere.core.constant.OrderDirection;
import org.apache.shardingsphere.core.parse.antlr.extractor.api.CollectionSQLSegmentExtractor;
import org.apache.shardingsphere.core.parse.antlr.extractor.util.ExtractorUtils;
import org.apache.shardingsphere.core.parse.antlr.extractor.util.RuleName;
import org.apache.shardingsphere.core.parse.antlr.sql.segment.dml.order.item.ColumnNameOrderByItemSegment;
import org.apache.shardingsphere.core.parse.antlr.sql.segment.dml.order.item.ExpressionOrderByItemSegment;
import org.apache.shardingsphere.core.parse.antlr.sql.segment.dml.order.item.IndexOrderByItemSegment;
import org.apache.shardingsphere.core.parse.antlr.sql.segment.dml.order.item.OrderByItemSegment;
import org.apache.shardingsphere.core.util.NumberUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Order by item extractor.
 *
 * @author zhangliang
 */
public final class OrderByItemExtractor implements CollectionSQLSegmentExtractor {
    
    @Override
    public Collection<OrderByItemSegment> extract(final ParserRuleContext ancestorNode, final Map<ParserRuleContext, Integer> parameterMarkerIndexes) {
        Collection<OrderByItemSegment> result = new LinkedList<>();
        for (ParserRuleContext each : ExtractorUtils.getAllDescendantNodes(ancestorNode, RuleName.ORDER_BY_ITEM)) {
            OrderDirection orderDirection = 2 == each.getChildCount() && OrderDirection.DESC.name().equalsIgnoreCase(each.getChild(1).getText()) ? OrderDirection.DESC : OrderDirection.ASC;
            Optional<ParserRuleContext> indexNode = ExtractorUtils.findFirstChildNode(each, RuleName.NUMBER_LITERALS);
            if (indexNode.isPresent()) {
                result.add(new IndexOrderByItemSegment(NumberUtil.getExactlyNumber(indexNode.get().getText(), 10).intValue(), orderDirection, OrderDirection.ASC));
                continue;
            }
            Optional<ParserRuleContext> expressionNode = ExtractorUtils.findFirstChildNode(each, RuleName.EXPR);
            if (expressionNode.isPresent()) {
                result.add(new ExpressionOrderByItemSegment(expressionNode.get().getText(), orderDirection, OrderDirection.ASC));
                continue;
            }
            Optional<ParserRuleContext> columnNameNode = ExtractorUtils.findFirstChildNode(each, RuleName.COLUMN_NAME);
            if (columnNameNode.isPresent()) {
                result.add(new ColumnNameOrderByItemSegment(columnNameNode.get().getText(), orderDirection, OrderDirection.ASC));
            }
        }
        return result;
    }
}
