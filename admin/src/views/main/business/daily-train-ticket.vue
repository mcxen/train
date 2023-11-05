<template>
  <p>
    <a-space>
      <train-select-view v-model="codeParam.trainCode" width="200px"/>
      <a-date-picker v-model:value="codeParam.date" valueFormat="YYYY-MM-DD" placeholder="请选择日期" />
      <station-select-view v-model="codeParam.start" width="200px"/>
      <station-select-view v-model="codeParam.end" width="200px"/>
      <a-button type="primary" @click="handleQuery()">查找</a-button>
      
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainTickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'station'">
        {{record.start}}<br/>
        {{record.end}}
      </template>
      <template v-else-if="column.dataIndex === 'time'">
        {{record.startTime}}<br/>
        {{record.endTime}}
      </template>
      <template v-else-if="column.dataIndex === 'duration'">
        {{calDuration(record.startTime, record.endTime)}}<br/>
        <div v-if="record.startTime.replaceAll(':', '') >= record.endTime.replaceAll(':', '')">
          次日到达
        </div>
        <div v-else>
          当日到达
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'ydz'">
        <div v-if="record.ydz >= 0">
          {{record.ydz}}<br/>
          {{record.ydzPrice}}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'edz'">
        <div v-if="record.edz >= 0">
          {{record.edz}}<br/>
          {{record.edzPrice}}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'rw'">
        <div v-if="record.rw >= 0">
          {{record.rw}}<br/>
          {{record.rwPrice}}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'yw'">
        <div v-if="record.yw >= 0">
          {{record.yw}}<br/>
          {{record.ywPrice}}￥
        </div>
        <div v-else>
          --
        </div>
      </template>
    </template>
  </a-table>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";
import TrainSelectView from "@/components/train-select.vue";

export default defineComponent({
  name: "daily-train-ticket-view",
  components: {TrainSelectView, StationSelectView},
  setup() {
    // 全局的window，直接引用import './assets/js/enums';//引入enums.js，
    const visible = ref(false);
    let dailyTrainTicket = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      start: undefined,
      startPinyin: undefined,
      startTime: undefined,
      startIndex: undefined,
      end: undefined,
      endPinyin: undefined,
      endTime: undefined,
      endIndex: undefined,
      ydz: undefined,
      ydzPrice: undefined,
      edz: undefined,
      edzPrice: undefined,
      rw: undefined,
      rwPrice: undefined,
      yw: undefined,
      ywPrice: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrainTickets = ref([]);
    // 分页的三个属性名是固定的，不能更改，是vue的ref的固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 6,
    });
    let codeParam = ref({
      trainCode: null,
      date:null,
      start:null,
      end:null
    });
    let loading = ref(false);
    const columns = [
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: '车次编号',
      dataIndex: 'trainCode',
      key: 'trainCode',
    },
    {
      title: '车站',
      dataIndex: 'station',
    },
    {
      title: '时间',
      dataIndex: 'time',
    },
    {
      title: '历时',
      dataIndex: 'duration',
    },
    // {
    //   title: '出发站',
    //   dataIndex: 'start',
    //   key: 'start',
    // },
    // {
    //   title: '出发站拼音',
    //   dataIndex: 'startPinyin',
    //   key: 'startPinyin',
    // },
    // {
    //   title: '出发时间',
    //   dataIndex: 'startTime',
    //   key: 'startTime',
    // },
    // {
    //   title: '出发站序',
    //   dataIndex: 'startIndex',
    //   key: 'startIndex',
    // },
    // {
    //   title: '到达站',
    //   dataIndex: 'end',
    //   key: 'end',
    // },
    // {
    //   title: '到达站拼音',
    //   dataIndex: 'endPinyin',
    //   key: 'endPinyin',
    // },
    // {
    //   title: '到站时间',
    //   dataIndex: 'endTime',
    //   key: 'endTime',
    // },
    // {
    //   title: '到站站序',
    //   dataIndex: 'endIndex',
    //   key: 'endIndex',
    // },
    {
      title: '一等座余票',
      dataIndex: 'ydz',
      key: 'ydz',
    },
    // {
    //   title: '一等座票价',
    //   dataIndex: 'ydzPrice',
    //   key: 'ydzPrice',
    // },
    {
      title: '二等座余票',
      dataIndex: 'edz',
      key: 'edz',
    },
    // {
    //   title: '二等座票价',
    //   dataIndex: 'edzPrice',
    //   key: 'edzPrice',
    // },
    {
      title: '软卧余票',
      dataIndex: 'rw',
      key: 'rw',
    },
    // {
    //   title: '软卧票价',
    //   dataIndex: 'rwPrice',
    //   key: 'rwPrice',
    // },
    {
      title: '硬卧余票',
      dataIndex: 'yw',
      key: 'yw',
    },
    // {
    //   title: '硬卧票价',
    //   dataIndex: 'ywPrice',
    //   key: 'ywPrice',
    // },
    ];

//如果没有初值的话就增加一个初始的参数首页为1页
    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/daily-train-ticket/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode:codeParam.value.trainCode,
          date:codeParam.value.date,
          start:codeParam.value.start,
          end:codeParam.value.end
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrainTickets.value = data.content.list;
          // 设置分页控件的值
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleTableChange = (page) => {
      // page是局部的变量，pagination是响应式的变量
      //增加点击的事件
      // console.log("看看自带的分页参数都有啥：" + pagination);
      pagination.value.pageSize=page.pageSize;
      handleQuery({
        page: page.current,
        size: page.pageSize
      });
    };
    // 计算停站的时间长度。
    const calDuration = (startTime, endTime) => {
      let diff = dayjs(endTime, 'HH:mm:ss').diff(dayjs(startTime, 'HH:mm:ss'), 'seconds');
      return dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    };
    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      dailyTrainTicket,
      visible,
      dailyTrainTickets,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      codeParam,
      calDuration
    };
  },
});
</script>