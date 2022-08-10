package com.easyink.common.core.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.core.domain.conversation.ChatInfoVO;
import com.easyink.common.core.domain.elastic.ElasticSearchEntity;
import com.easyink.common.exception.CustomException;
import com.easyink.common.utils.StringUtils;
import com.easyink.common.utils.spring.SpringUtils;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author admin
 * @description es工具类
 * @date 2020/12/9 14:02
 **/
@Slf4j
@Component
public class ElasticSearch {
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private static final String MSG_ID = "msgid";

    /**
     * @param idxName 索引名称
     * @param idxSQL  索引描述
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:30
     * @since
     */
    public void createIndex(String idxName, String idxSQL) {
        try {
            if (!this.indexExist(idxName)) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            buildSetting(request);
            request.mapping(idxSQL, XContentType.JSON);
//            request.settings() 手工指定Setting
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            CreateIndexResponse res = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new CustomException("初始化失败");
            }
        } catch (Exception e) {
            log.error("createIndex Exception ex:【{}】", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * @param idxName 索引名称
     * @param builder 索引描述
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:30
     * @since
     */
    public void createIndex2(String idxName, XContentBuilder builder) {
        try {
            //将索引名称改成小写
            if (StringUtils.isNotBlank(idxName)) {
                idxName = idxName.toLowerCase();
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            GetIndexRequest getIndexRequest = new GetIndexRequest(idxName);
            buildSetting(request);
            request.mapping(builder);
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            if (!exists) {
                CreateIndexResponse res = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
                if (!res.isAcknowledged()) {
                    log.info("初始化失败");
                }
            } else {
                log.info("idxName={} 已经存在,idxSql={}", idxName, builder);
            }
        } catch (Exception e) {
            log.error("创建es索引失败:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 断某个index是否存在
     *
     * @param idxName index名
     * @return boolean
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:27
     * @since
     */
    public boolean indexExist(String idxName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(idxName);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 设置分片
     *
     * @param request
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 19:27
     * @since
     */
    public void buildSetting(CreateIndexRequest request) {
        request.settings(Settings.builder().put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));
    }

    /**
     * @param idxName index
     * @param entity  对象
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:27
     * @since
     */
    public void insertOrUpdateOne(String idxName, ElasticSearchEntity entity) {
        IndexRequest request = new IndexRequest(idxName);
        log.info("Data : id={},entity={}", entity.getId(), JSON.toJSONString(entity.getData()));
        request.id(entity.getId());
        request.source(entity.getData(), XContentType.JSON);
        try {
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ES exception ex:{}", ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 批量插入数据
     *
     * @param idxName index
     * @param list    带插入列表
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:26
     * @since
     */
    public void insertBatch(String idxName, List<JSONObject> list) {
        BulkRequest request = new BulkRequest();

        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getString(MSG_ID))
                .source(item, XContentType.JSON)));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
                log.info("ES insertBatch res={}", JSON.toJSONString(bulk));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insertBatchEntity(String idxName, List<ElasticSearchEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getId())
                .source(item.getData(), XContentType.JSON)));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步批量插入，并执行回调方法
     *
     * @param idxName
     * @param list
     * @param consumer
     */
    public void insertBatchAsync(String idxName, List<ElasticSearchEntity> list, BiConsumer consumer, Object param) {
        BulkRequest request = new BulkRequest();
        list.parallelStream().forEach(item -> request.add(new IndexRequest(idxName).id(item.getId())
                .source(item.getData(), XContentType.JSON)));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                restHighLevelClient.bulkAsync(request, RequestOptions.DEFAULT, getActionListener(consumer, list, param));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insertBatchAsync(String idxName, List<JSONObject> list, Consumer<List<JSONObject>> consumer) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getString(MSG_ID))
                .source(item, XContentType.JSON)));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                restHighLevelClient.bulkAsync(request, RequestOptions.DEFAULT, getActionListener(consumer, list));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBatch(String idxName, List<ElasticSearchEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(
//                new UpdateRequest(idxName,"doc", item.getId()).upsert(item.getData(), XContentType.JSON))
                new UpdateRequest(idxName, item.getId()).doc(item.getData(), XContentType.JSON))
        );
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBatchByJson(String idxName, List<ChatInfoVO> list) {
        BulkRequest request = new BulkRequest();

        list.forEach(item -> request.add(new UpdateRequest(idxName, item.getMsgid())
                .doc(JSONObject.toJSONString(item),XContentType.JSON)));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除
     *
     * @param idxName index
     * @param idList  待删除列表
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:14
     * @since
     */
    public <T> void deleteBatch(String idxName, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(idxName, item.toString())));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param idxName index
     * @param builder 查询参数
     * @param c       结果类对象
     * @return java.util.List<T>
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:14
     * @since
     */
    public <T> List<T> search(String idxName, SearchSourceBuilder builder, Class<T> c) {
        try {
            this.createIndex2(idxName, this.getFinanceMapping());
        } catch (IOException e) {
            log.error("创建es索引异常:{}", ExceptionUtils.getStackTrace(e));
        }
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (ConnectException e) {
            throw new CustomException("该系统未安装会话存档工具，请联系系统管理员或客服");
        } catch (Exception e) {
            log.error("es搜索出现错误:{}", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    public <T> PageInfo<T> searchPage(String idxName, SearchSourceBuilder builder, int pageNum, int pageSize, Class<T> c) {
        try {
            this.createIndex2(idxName, this.getFinanceMapping());
        } catch (IOException e) {
            log.error("创建es索引异常:{}", ExceptionUtils.getStackTrace(e));
        }
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            int totalHits = (int) response.getHits().getTotalHits().value;
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                //解析高亮字段
                //获取当前命中的对象的高亮的字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField hghlightContent = highlightFields.get("text.content");
                StringBuilder newName = new StringBuilder();
                if (hghlightContent != null) {
                    //获取该高亮字段的高亮信息
                    Text[] fragments = hghlightContent.getFragments();
                    //将前缀、关键词、后缀进行拼接
                    for (Text fragment : fragments) {
                        newName.append(fragment.toString());
                    }
                }
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("content", newName.toString());
                res.add(JSON.parseObject(JSON.toJSONString(sourceAsMap), c));
            }
            // 封装分页
            PageInfo<T> page = new PageInfo<>();
            page.setList(res);
            page.setPageNum(pageNum);
            page.setPageSize(pageSize);
            page.setTotal(totalHits);
            int pages = totalHits;
            if (totalHits != 0 && totalHits % pageNum == 0) {
                pages = totalHits / pageNum;
            } else if (totalHits != 0 && totalHits % pageNum != 0) {
                pages = (totalHits / pageNum) + 1;
            }
            page.setPages(pages);
            page.setHasNextPage(page.getPageNum() < page.getPages());
            return page;
        } catch (ConnectException e) {
            throw new CustomException("该系统未安装会话存档工具，请联系系统管理员或客服");
        } catch (Exception e) {
            log.error("ES查询分页异常：e:{}", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除index
     *
     * @param idxName
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:13
     * @since
     */
    public void deleteIndex(String idxName) {
        try {
            if (!this.indexExist(idxName)) {
                return;
            }
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            restHighLevelClient.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param idxName
     * @param builder
     * @return void
     * @throws
     * @author admin
     * @See
     * @date 2019/10/17 17:13
     * @since
     */
    public void deleteByQuery(String idxName, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ActionListener getActionListener(Consumer consumer, List<JSONObject> list) {
        return new ActionListener() {
            @Override
            public void onResponse(Object o) {
                threadPoolTaskExecutor.submit(() -> consumer.accept(list));
            }

            @Override
            public void onFailure(Exception e) {
                log.warn("work with es failed, exception={}", ExceptionUtils.getStackTrace(e));
            }
        };
    }

    public ActionListener getActionListener(BiConsumer consumer, List<ElasticSearchEntity> list, Object param) {
        return new ActionListener() {
            @Override
            public void onResponse(Object o) {
                threadPoolTaskExecutor.submit(() -> consumer.accept(list, param));
            }

            @Override
            public void onFailure(Exception e) {
                log.warn("work with es failed, exception={}", ExceptionUtils.getStackTrace(e));
            }
        };
    }

    public XContentBuilder getFinanceMapping() throws IOException {
        String keyword = "keyword";
        String properties = "properties";
        String type = "type";
        String seq = "seq";
        String action = "action";
        String roomid = "roomid";
        String msgtime = "msgtime";
        String msgtype = "msgtype";
        String longString = "long";
        String from = "from";
        // 创建 会话文本Mapping
        return XContentFactory.jsonBuilder()
                .startObject()
                .startObject(properties)
                .startObject(MSG_ID)
                .field(type, keyword)
                .endObject()
                .startObject(seq)
                .field(type, longString)
                .endObject()
                .startObject(action)
                .field(type, keyword)
                .endObject()
                .startObject(from)
                .field(type, keyword)
                .endObject()
                .startObject(roomid)
                .field(type, keyword)
                .endObject()
                .startObject(msgtime)
                .field(type, longString)
                .endObject()
                .startObject(msgtype)
                .field(type, keyword)
                .endObject()
                .endObject()
                .endObject();
    }

    public void insertBatch1(String idxName, List<ChatInfoVO> list) {
        BulkRequest request = new BulkRequest();

        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getMsgid())
                .source(JSONObject.toJSONString(item),XContentType.JSON)));
        try {
            if (!CollectionUtils.isEmpty(request.requests())) {
                RestHighLevelClient restHighLevelClient = SpringUtils.getBean(RestHighLevelClient.class);
                BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
                log.info("ES insertBatch res={}", JSON.toJSONString(bulk));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}