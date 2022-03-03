<template>
    <div id="app" @touchmove.prevent>   <!-- 禁止上下滑动-->
        <router-view v-if="isRouterAlive"></router-view>
    </div>
</template>

<script>
    window.onload = function () {
        document.addEventListener('touchstart', function (event) {
            if (event.touches.length > 1) {
                event.preventDefault();
            }
        });

        var lastTouchEnd = 0;

        document.addEventListener('touchend', function (event) {
            var now = (new Date()).getTime();
            if (now - lastTouchEnd <= 300) {
                event.preventDefault();
            }
            lastTouchEnd = now;

        }, false);

        document.addEventListener('gesturestart', function (event) {
            event.preventDefault();
        });
    }
    export default {
        name: 'App',
        provide() {
            return {
                reload: this.reload
            }
        },
        data() {
            return {
                isRouterAlive: true
            }
        },
        methods: {
            reload() {
                this.isRouterAlive = false
                this.$nextTick(function () {
                    this.isRouterAlive = true
                })
            }
        },
        components: {}
    }
</script>