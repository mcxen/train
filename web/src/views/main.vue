<template>
    <a-layout id="components-layout-demo-top-side-2">
<!--       导入头部导航栏 -->
        <the-header-view></the-header-view>
        <a-layout>
          <the-sider-view/>
            <a-layout style="padding: 0 24px 24px">

                <a-layout-content
                    :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
                >
<!--                  根据子路由来决定content显示什么-->
                  <router-view></router-view>
                </a-layout-content>
            </a-layout>
        </a-layout>
    </a-layout>
</template>
<script>
import { defineComponent, ref } from 'vue';
import TheHeaderView from "@/components/the-header.vue";
import TheSiderView from "@/components/the-sider.vue";
export default defineComponent({
    components: {
      TheSiderView,
      TheHeaderView,

    },
    setup() {

        return {
        };
    },
});
</script>
<style>

</style>