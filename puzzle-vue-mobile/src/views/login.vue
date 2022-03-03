<template>
    <div class="login">
        <img class="backimage" src="../img/my4.jpg"/>  <!--背景-->

        <div class="puzzle-title"><h2>Puzzle</h2></div>

        <div class="el-textarea el-input--default idinput">
            <textarea class="el-textarea__inner" rows="1" autocomplete="off" placeholder="在 Puzzle 上玩耍, 请先输入昵称"
                      style="min-height: 40px" v-model="username"
                      @keyup.enter.native="login()"></textarea>

            <div class="container">
                <div class="tile-icon-in" @click="login()">
                    <img src="../img/in.png" :style="{width: '24px', height: '24px'}">
                </div>

                <div class="tile-icon-out" @click="close">
                    <img src="../img/cha.png" :style="{width: '24px', height: '24px'}">
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    import Store from '@/store/store';

    export default {
        name: 'login',
        data() {
            return {
                username: ''
            }
        },
        methods: {
            close() {
                var userAgent = navigator.userAgent;
                if (userAgent.indexOf("Firefox") != -1 || userAgent.indexOf("Chrome") != -1) {
                    undefined
                    window.location.href = "about:blank"
                } else {
                    undefined
                    window.opener = null;
                    window.open("about:blank", "_self");
                    window.close();
                }
            },
            login() {     //与后端java交互，把ruleForm传给后端，通过后端返回的值 跳转到Puzzle界面。done
                this.username.replace(/[\r\n]/g, "");
                const ruleForm = {
                    username: this.username.replace(/[\r\n]/g, "")
                }
                const _this = this
                this.$axios.post('http://106.12.119.247:8081/user/login', ruleForm).then(res => {
                        //console.log(res.data)
                        Store.id = res.data.data.id
                        Store.username = res.data.data.username;
                        Store.status = res.data.data.status;
                        Store.first = res.data.data.status;

                        const url = "http://106.12.119.247:8081/puzzle/level/" + Store.status
                        this.$axios.get(url).then(res => {
                                //console.log(res.data)
                                Store.N = res.data.data.n
                                Store.Type = res.data.data.type
                                Store.src = res.data.data.imgSrc
                                Store.answer = res.data.data.answer
                                Store.puzzle_group = res.data.data.puzzleG
                                return this.$router.push('/puzzle')
                            }
                        )
                    }
                )

            }
        }
    }
</script>

<style>
    .puzzle-title {
        font-size: 40px;
        text-align: center;
        color: black;
        position: absolute;
        top: 6rem;
        left: 0;
        bottom: 0;
        right: 0;
        margin: auto;
        -moz-user-select: none; /*图片不可被选中*/
        -webkit-user-select: none;
        -ms-user-select: none;
        -khtml-user-select: none;
        user-select: none;
    }

    textarea.el-textarea__inner {
        border-radius: 30px; /*输入框圆角值*/
        line-height: 2;
        text-align: center;
    }

    div.el-textarea {
        position: absolute;
        top: 15rem;
        left: 0;
        bottom: 0;
        right: 0;
        margin: auto;
        width: 70%;
    }

    .container{
        display: flex;
        justify-content: center;
    }

    .tile-icon-in {
        align-items: center;
        background-color: #f1f3f4;
        border-radius: 50%;
        display: flex;
        flex-shrink: 0;
        height: 48px;
        justify-content: center;
        margin-top: 2rem;
        width: 48px;
    }

    .tile-icon-out {
        align-items: center;
        background-color: #f1f3f4;
        border-radius: 50%;
        display: flex;
        flex-shrink: 0;
        height: 48px;
        justify-content: center;
        margin-top: 2rem;
        width: 48px;
        margin-left: 3rem;
    }

    html, body, #app {
        height: 99%;
    }
</style>