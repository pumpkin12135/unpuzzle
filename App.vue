<template>
    <div class="game" :style="{width: canvasWidth, height: '100%'}">
        <img class="backimage" src="./img/my2.png"/>  <!--背景-->
        <div class="pintu_area" :style="{width: `${this.N * 60}px`, height: `${this.N * 60}px`}">  <!--拼图区域、右键工具区-->
            <img src="./img/test2.png" class="test"/>
            <div v-for="(part, index) in partList" :key="'part' + index" class="pintu_part"
                 @contextmenu.prevent="opentips(index)">
                <img v-show="partList[index].type1" src="./img/cha.png" class="pingtu_cha">
                <img v-show="partList[index].type2" src="./img/zheng.png" class="pingtu_cha">
            </div>
        </div>

        <div class="puzzle">  <!--拼图-->
            <div v-for="(puzzle1, index) in puzzleList[0]" :key="puzzle1.id" class="part_img1"
                 :style="{'backgroundImage': `url(${this.image})`, 'backgroundPosition': `100px 200px`}"
                 @mousedown="moveImg($event,0, index)" @contextmenu.prevent="openMenu($event, 0, index)">
            </div>
            <div v-for="(puzzle2, index) in puzzleList[1]" :key="puzzle2.id" class="part_img2"
                 :style="{'backgroundImage': `url(${this.image})`, 'backgroundPosition': `600px 200px`}"
                 @mousedown="moveImg($event,1, index)" @contextmenu.prevent="openMenu($event, 1, index)">
            </div>
            <div v-for="(puzzle3, index) in puzzleList[2]" :key="puzzle3.id" class="part_img3"
                 :style="{'backgroundImage': `url(${this.image})`, 'backgroundPosition': `600px 0px`}"
                 @mousedown="moveImg($event,2, index)" @contextmenu.prevent="openMenu($event, 2, index)">
            </div>
            <div v-for="(puzzle4, index) in puzzleList[3]" :key="puzzle4.id" class="part_img4"
                 :style="{'backgroundImage': `url(${this.image})`, 'backgroundPosition': `100px 200px`}"
                 @mousedown="moveImg($event,3, index)" @contextmenu.prevent="openMenu($event, 3, index)">
            </div>
        </div>

        <img class="thumb" src="./img/puzzle.jpg" @contextmenu.prevent>  <!--右上角图片-->

        <div class="pick"> <!--选择、游戏说明、确认-->
            <div style="height: 300px;">
                <el-steps direction="vertical" :active="pid">
                    <div class="infinite-list" v-infinite-scroll="load" style="overflow:auto" id="parent">
                        <el-step style="height: 100px;" v-for="(part, index) in pickList" :title=pickList[index].title
                                 description="Jigsaw puzzle welcome you to play"></el-step>
                    </div>
                </el-steps>
            </div>

            <el-button :style="{marginTop: '10%', marginLeft: '20%'}" plain @click="finalgame()">确认提交</el-button>

            <el-button :style="{marginTop: '10%', marginLeft: '10%'}" plain @click="dialogTableVisible = true">游戏说明
                <el-dialog width="700px" v-model="dialogTableVisible" :showClose="false">
                    <img class="play" src="./img/play.png" @contextmenu.prevent>
                </el-dialog>
            </el-button>
        </div>

        <ul v-show="visible" :style="{left:left-50+'px', top:top-100+'px'}" class="contextmenu" @contextmenu.prevent>  <!--右键删除旋转对称-->
            <img src="./img/use.png" class="img1"/>
            <img src="./img/rotate.png" @click="rotateImg(target, rightClickIndex, rightClickType)" class="img2"/>
            <img src="./img/duichen.png" @click="reveseImg(target, rightClickIndex, rightClickType)" class="img3"/>
            <img src="./img/delete.png" @click="deleteImg(rightClickIndex, rightClickType)" class="img4"/>
        </ul>
    </div>
</template>

<script>
    //TODO
    //  1.UI美化                       2022.2.17   done
    //  2.确认提交                      2022.2.17   done
    //  3.能够选关卡                    2022.2.18   done
    //  3.数据与java后端交互             2022.2.18
    //  4.手机操作化                    2022.2.19
    //  5.上传服务器 域名指向服务器地址     2022.2.20
    //TODO BUG 左右键一起按的时候 暂不改
    //TODO BUG 旋转对称有看起来不和谐的地方 先对称再旋转 不改
    //TODO 和后端数据交互 1. n * n的拼图区域  2. 游戏图片 3.有多少关卡和已通过的个数 4.
    export default {
        name: 'Canvas',
        data() {
            return {
                canvasWidth: document.documentElement.clientWidth,   //600 1400   500 1000
                canvasHeight: document.documentElement.clientHeight,
                //右键菜单
                visible: false,
                top: 0,
                left: 0,
                rightClickType: -1,
                rightClickIndex: -1,
                target: {},  //用于存放上一个
                //拼图区域
                N: 6,   //6*6的大小
                src: 'https://github.com/pumpkin12135/unpuzzle/blob/main/img/test1.png',
                partList: [], // 卡槽
                checkList: [-1, -1, -1, -1, -1, -1], //看有多少块
                finalList: [[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1]],
                realanswer : [0, 1, 2, 3, 4, 5, 7, 9, 11, 12, 14, 15, 16, 18, 19, 20, 22, 24, 26, 29, 32, 33, 34, 35],
                //拼图
                puzzleList: [[{id: 0, rotate: 0, revese: false, band: -1}], [{id: 0, rotate: 0, revese: false, band: -1}],
                    [{id: 0, rotate: 0, revese: false, band: -1}], [{id: 0, rotate: 0, revese: false, band: -1}]],
                iList: [1, 1, 1, 1],
                zi: 1,
                //关卡
                pickList: [{title: "关卡1"}, {title: "关卡2"}, {title: "关卡3"}, {title: "关卡4"}, {title: "关卡5"}, {title: "关卡6"}],
                pid: 0,   //第几关

                rotateStatus: true, // 拼图旋转状态
                dialogTableVisible: false,
                image: 'http://tva1.sinaimg.cn/mw1024/006yt1Omgy1gxkooa7b2mj31kn14gtt8.jpg' // 拼图图片路径
            }
        },
        watch: {
            //监听长度和宽度 自适应大小
            canvasHeight(val) {
                // 为了避免频繁触发resize函数导致页面卡顿，使用定时器
                if (!this.timer) {
                    // 一旦监听到的screenWidth值改变，就将其重新赋给data里的screenWidth
                    this.canvasHeight = val
                    this.timer = true
                    let that = this
                    setTimeout(function () {
                        // 打印screenWidth变化的值
                        //console.log(that.canvasHeight)
                        that.timer = false
                    }, 400)
                }
            },
            canvasWidth(val) {
                // 为了避免频繁触发resize函数导致页面卡顿，使用定时器
                if (!this.timer) {
                    // 一旦监听到的screenWidth值改变，就将其重新赋给data里的screenWidth
                    this.canvasWidth = val
                    this.timer = true
                    let that = this
                    setTimeout(function () {
                        // 打印screenWidth变化的值
                        //console.log(that.canvasWidth)
                        that.timer = false
                    }, 400)
                }
            },
            visible(value) {
                if (value) {
                    document.body.addEventListener('click', this.closeMenu)
                } else {
                    document.body.removeEventListener('click', this.closeMenu)
                }
            },
            pid() {
                if (this.pid >= 2) {
                    var parent = document.getElementById('parent');
                    parent.scrollTop += 100
                }
            }
        },
        mounted() {
            const that = this
            window.onresize = () => {
                return (() => {
                    that.canvasHeight = document.documentElement.clientHeight - 22
                    that.canvasWidth = document.documentElement.clientWidth - 10
                })()
            }
            this.initGame()
        },
        methods: {
            initGame() { // 游戏初始化
                this.partList = []
                for (let i = 0; i < this.N * this.N; i++) { // 循环遍历生成拼图数组与对应卡槽数组

                    this.partList.push({ // 卡槽数组 倒着push的
                        x: this.N - i % this.N, // 第几列
                        y: this.N - parseInt((i / this.N)), //第几行
                        group: -1,
                        type1: false,
                        type2: false
                    })
                }
                this.partList[0].group = this.partList[1].group = this.partList[2].group = this.partList[7].group = 0
                this.partList[3].group = this.partList[4].group = this.partList[5].group = this.partList[11].group =
                    this.partList[17].group = this.partList[23].group = 1
                this.partList[6].group = this.partList[12].group = this.partList[18].group = this.partList[19].group =
                    this.partList[24].group = 2
                this.partList[8].group = this.partList[9].group = this.partList[10].group = this.partList[15].group =
                    this.partList[16].group = this.partList[22].group = this.partList[28].group = 3
                this.partList[13].group = this.partList[14].group = this.partList[20].group = this.partList[21].group =
                    this.partList[25].group = this.partList[26].group = this.partList[27].group =
                        this.partList[30].group = this.partList[31].group = this.partList[32].group = 4
                this.partList[29].group = this.partList[33].group = this.partList[34].group = this.partList[35].group = 5
            },
            //移动 翻转 反转 删除 拼图
            moveImg(e, type, index) { // 移动拼图
                const _this = this
                if (e.button < 1) { // 判断当前卡片未放置到正确位置切是左键点击
                    if (this.puzzleList[type][index].id == this.iList[type] - 1) {  //复制一个进入puzzleList
                        _this.puzzleList[type].push({id: this.iList[type]++, rotate: 0, revese: false, band: -1})
                    }

                    const el = e.target
                    el.style.transition = "none"
                    el.style.zIndex = 300 + this.zi + 1;  //这里保证左键拼图始终在最上层
                    const sX = e.clientX - el.offsetLeft  //用点击的点减去初始值 代表在图形内的相对位置 即相对第一个点的位置
                    const sY = e.clientY - el.offsetTop

                    if (this.puzzleList[type][index].band != -1) {   //出现移动 旋转 对称 删除时首先先进行这条语句
                        this.checkList[this.puzzleList[type][index].band] = -1;
                        this.finalList[this.puzzleList[type][index].band][0] = -1
                        this.finalList[this.puzzleList[type][index].band][1] = -1
                        this.finalList[this.puzzleList[type][index].band][2] = -1
                        this.finalList[this.puzzleList[type][index].band][3] = -1
                    }

                    document.onmousemove = (e) => { // 拼图随鼠标移动,改变拼图的top和left属性值 设置边界
                        const eX = e.clientX - sX
                        const eY = e.clientY - sY
                        let wid_r = 0
                        let hei_t = 0
                        let wid_l = 0
                        let hei_d = 0
                        if (type == 0 && this.puzzleList[type][index].rotate == 0) {
                            wid_l = 0;
                            wid_r = 240;
                            hei_t = 0;
                            hei_d = 60;
                        } else if (type == 0 && this.puzzleList[type][index].rotate == 0.25) {
                            wid_l = 60;
                            wid_r = 120;
                            hei_t = -120;
                            hei_d = 120;
                        } else if (type == 0 && this.puzzleList[type][index].rotate == 0.5) {
                            wid_l = 0;
                            wid_r = 240;
                            hei_t = -60;
                            hei_d = 0;
                        } else if (type == 0 && this.puzzleList[type][index].rotate == 0.75) {
                            wid_l = 120;
                            wid_r = 180;
                            hei_t = -120;
                            hei_d = 120;
                        } else if (type == 1 && this.puzzleList[type][index].rotate == 0) {
                            wid_l = 0;
                            wid_r = 180;
                            hei_t = 0;
                            hei_d = 120;
                        } else if (type == 1 && this.puzzleList[type][index].rotate == 0.25) {
                            wid_l = 0;
                            wid_r = 120;
                            hei_t = 0;
                            hei_d = 180;
                        } else if (type == 1 && this.puzzleList[type][index].rotate == 0.5) {
                            wid_l = -60;
                            wid_r = 120;
                            hei_t = 0;
                            hei_d = 120;
                        } else if (type == 1 && this.puzzleList[type][index].rotate == 0.75) {
                            wid_l = 0;
                            wid_r = 120;
                            hei_t = -60;
                            hei_d = 120;
                        } else if (type == 2 && this.puzzleList[type][index].rotate == 0) {
                            wid_l = 0;
                            wid_r = 120;
                            hei_t = 0;
                            hei_d = 180;
                        } else if (type == 2 && this.puzzleList[type][index].rotate == 0.25) {
                            wid_l = 0;
                            wid_r = 180;
                            hei_t = 60;
                            hei_d = 180;
                        } else if (type == 2 && this.puzzleList[type][index].rotate == 0.5) {
                            wid_l = 0;
                            wid_r = 120;
                            hei_t = 60;
                            hei_d = 240;
                        } else if (type == 2 && this.puzzleList[type][index].rotate == 0.75) {
                            wid_l = -60;
                            wid_r = 120;
                            hei_t = 60;
                            hei_d = 180;
                        } else if (type == 3 && this.puzzleList[type][index].rotate == 0) {
                            wid_l = 0;
                            wid_r = 120;
                            hei_t = 0;
                            hei_d = 180;
                        } else if (type == 3 && this.puzzleList[type][index].rotate == 0.25) {
                            wid_l = -60;
                            wid_r = 120;
                            hei_t = 0;
                            hei_d = 120;
                        } else if (type == 3 && this.puzzleList[type][index].rotate == 0.5) {
                            wid_l = 0;
                            wid_r = 120;
                            hei_t = -60;
                            hei_d = 120;
                        } else {
                            wid_l = 0;
                            wid_r = 180;
                            hei_t = 0;
                            hei_d = 120;
                        }
                        if (eX + wid_l > 0 && eX < this.canvasWidth - wid_r) {
                            el.style.left = eX + 'px'
                        }
                        if (eY + hei_t > 0 && eY < this.canvasHeight - hei_d) {
                            el.style.top = eY + 'px'
                        }
                    }
                    document.onmouseup = (e) => {
                        const marginleft = (this.canvasWidth - this.N * 60) / 2
                        const margintop = (this.canvasHeight - this.N * 60) / 2
                        //如果没有移动到拼图区 则删除
                        if (e.button < 1 && e.target.style.backgroundImage == 'url("http://tva1.sinaimg.cn/mw1024/006yt1Omgy1gxkooa7b2mj31kn14gtt8.jpg")'
                            && (e.clientX - sX < marginleft - 60 || e.clientY - sY < margintop - 60 || e.clientX - sX > marginleft + 360 || e.clientY - sY > margintop + 360)
                            && this.puzzleList[type][index].id != this.iList[type] - 1) {
                            //这里好像有个莫名其妙的bug，所以用了target.id的判断 且必须是左键点击  还有一些莫名其妙的bug 会导致原始元素的被迫删除加入了最后一个判断
                            this.deleteImg(index, type)
                            return
                        }

                        document.onmousemove = null
                        el.style.transition = "all 1s"
                        el.style.zIndex = 200 + this.zi++  //这里保证所有新移动完成的拼图处于所有已定拼图中的最上层

                        if (this.checkImg(el, type, index)) {  //就近判断 就吸附到那个区域 同时这个区域开启锁定
                            const left = (parseInt(el.style.left)  - marginleft) / 60
                            const top = (parseInt(el.style.top) - margintop) / 60
                            const toLeft = Math.round(left) < this.N-0.5 ? Math.round(left) : this.N-1
                            const toTop = Math.round(top) < this.N-0.5 ? Math.round(top) : this.N-1

                            el.style.left = toLeft * 60 + marginleft + 'px'
                            el.style.top = toTop * 60 + margintop + 'px'
                        }

                    }
                }
            },
            rotateImg(e, index, type) { // 旋转当前拼图
                if (this.rotateStatus) { // 拼图可以旋转并且并未正确放置
                    if (this.puzzleList[type][index].band != -1) {   //出现移动 旋转 对称 删除时首先先进行这条语句
                        this.checkList[this.puzzleList[type][index].band] = -1;
                        this.finalList[this.puzzleList[type][index].band][0] = -1
                        this.finalList[this.puzzleList[type][index].band][1] = -1
                        this.finalList[this.puzzleList[type][index].band][2] = -1
                        this.finalList[this.puzzleList[type][index].band][3] = -1
                    }

                    const el = e
                    this.rotateStatus = false
                    el.style.transition = "all 1s"

                    let angle = this.puzzleList[type][index].rotate
                    this.puzzleList[type][index].rotate += 0.25

                    if (angle < 0) { // 计算出的角度为270度时会返回-0.25，将其转为0.75以实现正确的旋转
                        angle = 0.75
                    }
                    if (this.puzzleList[type][index].revese) {   //反转过
                        el.style.transform = `rotate(${angle + 0.25}turn)` + `scale(-1, 1)`
                    } else {                            //没有反转过
                        el.style.transform += `rotate(0.25turn)`
                    }
                    //this.checkImg(e, type, index)
                    if (this.checkImg(e, type, index)) {  //就近判断 就吸附到那个区域 同时这个区域开启锁定
                        const marginleft = (this.canvasWidth - this.N * 60) / 2
                        const margintop = (this.canvasHeight - this.N * 60) / 2
                        const left = (parseInt(el.style.left)  - marginleft) / 60
                        const top = (parseInt(el.style.top) - margintop) / 60
                        const toLeft = Math.round(left) < this.N-0.5 ? Math.round(left) : this.N-1
                        const toTop = Math.round(top) < this.N-0.5 ? Math.round(top) : this.N-1

                        el.style.left = toLeft * 60 + marginleft + 'px'
                        el.style.top = toTop * 60 + margintop + 'px'
                    }
                    const _this = this
                    setTimeout(() => {
                        if (angle + 0.25 == 1) { // 如果旋转了360度将其重置为0度，不然再次旋转会变成逆时针旋转，然后检查该拼图是否正确的放置在正确的卡槽里
                            this.puzzleList[type][index].rotate = 0

                            el.style.transition = "none"
                            if (this.puzzleList[type][index].revese) {   //反转过
                                el.style.transform = `rotate(0turn)` + `scale(-1, 1)`
                            } else {                            //没有反转过
                                el.style.transform = `rotate(0turn)`
                            }

                        }
                        _this.rotateStatus = true
                    }, 500);
                }
            },
            reveseImg(e, index, type) {  //反转拼图 轴对称
                if (type == 2 || type == 3) {
                    if (this.puzzleList[type][index].band != -1) {   //出现移动 旋转 对称 删除时首先先进行这条语句
                        this.checkList[this.puzzleList[type][index].band] = -1;
                        this.finalList[this.puzzleList[type][index].band][0] = -1
                        this.finalList[this.puzzleList[type][index].band][1] = -1
                        this.finalList[this.puzzleList[type][index].band][2] = -1
                        this.finalList[this.puzzleList[type][index].band][3] = -1
                    }

                    if (this.puzzleList[type][index].revese) {   //反转过
                        let angle = this.puzzleList[type][index].rotate
                        e.style.transform = `rotate(${angle}turn)`
                        this.puzzleList[type][index].revese = false;
                    } else {                            //没有反转过
                        e.style.transform += `scale(-1, 1)`
                        this.puzzleList[type][index].revese = true;
                    }

                    if (this.checkImg(e, type, index)) {  //就近判断 就吸附到那个区域 同时这个区域开启锁定
                        const marginleft = (this.canvasWidth - this.N * 60) / 2
                        const margintop = (this.canvasHeight - this.N * 60) / 2
                        const left = (parseInt(e.style.left)  - marginleft) / 60
                        const top = (parseInt(e.style.top) - margintop) / 60
                        const toLeft = Math.round(left) < this.N - 0.5 ? Math.round(left) : this.N - 1
                        const toTop = Math.round(top) < this.N - 0.5 ? Math.round(top) : this.N - 1

                        e.style.left = toLeft * 60 + marginleft + 'px'
                        e.style.top = toTop * 60 + margintop + 'px'
                    }
                }
            },
            deleteImg(index, type) {
                if (this.puzzleList[type][index].band != -1) { //出现移动 旋转 对称 删除时首先先进行这条语句
                    this.checkList[this.puzzleList[type][index].band] = -1;
                    this.finalList[this.puzzleList[type][index].band][0] = -1
                    this.finalList[this.puzzleList[type][index].band][1] = -1
                    this.finalList[this.puzzleList[type][index].band][2] = -1
                    this.finalList[this.puzzleList[type][index].band][3] = -1
                }
                this.puzzleList[type].splice(index, 1)
            },

            checkImg(e, type, index) { // 检查puzzle是否在区域里    移动后将原来的true改为false
                const left = (parseInt(e.style.left)  - (this.canvasWidth - this.N * 60) / 2) / 60
                const top = (parseInt(e.style.top) - (this.canvasHeight - this.N * 60) / 2) / 60
                const toLeft = Math.round(left) < this.N - 0.5 ? Math.round(left) : this.N - 1
                const toTop = Math.round(top) < this.N - 0.5 ? Math.round(top) : this.N - 1
                const i = toTop * this.N + toLeft

                let a = 0, b = 0, c = 0, d = 0
                if (type == 0 && this.puzzleList[type][index].rotate == 0) {
                    a = i;
                    b = i + 1;
                    c = i + 2;
                    d = i + 3;
                }
                if (type == 0 && this.puzzleList[type][index].rotate == 0.25) {
                    a = i - 11;
                    b = i - 5;
                    c = i + 1;
                    d = i + 7;
                }
                if (type == 0 && this.puzzleList[type][index].rotate == 0.5) {
                    a = i - 6;
                    b = i - 5;
                    c = i - 4;
                    d = i - 3;
                }
                if (type == 0 && this.puzzleList[type][index].rotate == 0.75) {
                    a = i - 10;
                    b = i - 4;
                    c = i + 2;
                    d = i + 8;
                }
                if (type == 1 && this.puzzleList[type][index].rotate == 0) {
                    a = i + 1;
                    b = i + 6;
                    c = i + 7;
                    d = i + 8;
                }
                if (type == 1 && this.puzzleList[type][index].rotate == 0.25) {
                    a = i;
                    b = i + 6;
                    c = i + 7;
                    d = i + 12;
                }
                if (type == 1 && this.puzzleList[type][index].rotate == 0.5) {
                    a = i - 1;
                    b = i;
                    c = i + 1;
                    d = i + 6;
                }
                if (type == 1 && this.puzzleList[type][index].rotate == 0.75) {
                    a = i - 5;
                    b = i;
                    c = i + 1;
                    d = i + 7;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0 && !this.puzzleList[type][index].revese) {
                    a = i;
                    b = i + 6;
                    c = i + 12;
                    d = i + 13;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0.25 && !this.puzzleList[type][index].revese) {
                    a = i + 6;
                    b = i + 7;
                    c = i + 8;
                    d = i + 12;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0.5 && !this.puzzleList[type][index].revese) {
                    a = i + 6;
                    b = i + 7;
                    c = i + 13;
                    d = i + 19;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0.75 && !this.puzzleList[type][index].revese) {
                    a = i + 7;
                    b = i + 11;
                    c = i + 12;
                    d = i + 13;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0 && this.puzzleList[type][index].revese) {
                    a = i + 1;
                    b = i + 7;
                    c = i + 12;
                    d = i + 13;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0.25 && this.puzzleList[type][index].revese) {
                    a = i + 6;
                    b = i + 12;
                    c = i + 13;
                    d = i + 14;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0.5 && this.puzzleList[type][index].revese) {
                    a = i + 6;
                    b = i + 7;
                    c = i + 12;
                    d = i + 18;
                }
                if (type == 2 && this.puzzleList[type][index].rotate == 0.75 && this.puzzleList[type][index].revese) {
                    a = i + 5;
                    b = i + 6;
                    c = i + 7;
                    d = i + 13;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0 && !this.puzzleList[type][index].revese) {
                    a = i + 1;
                    b = i + 6;
                    c = i + 7;
                    d = i + 12;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0.25 && !this.puzzleList[type][index].revese) {
                    a = i - 1;
                    b = i;
                    c = i + 6;
                    d = i + 7;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0.5 && !this.puzzleList[type][index].revese) {
                    a = i - 5;
                    b = i;
                    c = i + 1;
                    d = i + 6;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0.75 && !this.puzzleList[type][index].revese) {
                    a = i;
                    b = i + 1;
                    c = i + 7;
                    d = i + 8;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0 && this.puzzleList[type][index].revese) {
                    a = i;
                    b = i + 6;
                    c = i + 7;
                    d = i + 13;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0.25 && this.puzzleList[type][index].revese) {
                    a = i;
                    b = i + 1;
                    c = i + 5;
                    d = i + 6;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0.5 && this.puzzleList[type][index].revese) {
                    a = i - 6;
                    b = i;
                    c = i + 1;
                    d = i + 7;
                }
                if (type == 3 && this.puzzleList[type][index].rotate == 0.75 && this.puzzleList[type][index].revese) {
                    a = i + 1;
                    b = i + 2;
                    c = i + 6;
                    d = i + 7;
                }

                if (a >= 0 && a <= 35 && b >= 0 && b <= 35 && c >= 0 && c <= 35 && d >= 0 && d <= 35) {
                    if (this.checkList[this.partList[a].group] == -1 && this.partList[a].group == this.partList[b].group && this.partList[b].group == this.partList[c].group && this.partList[c].group == this.partList[d].group) {
                        this.checkList[this.partList[a].group] = type
                        this.puzzleList[type][index].band = this.partList[a].group
                        this.finalList[this.partList[a].group][0] = a
                        this.finalList[this.partList[a].group][1] = b
                        this.finalList[this.partList[a].group][2] = c
                        this.finalList[this.partList[a].group][3] = d
                        return true
                    }
                } else {
                    this.puzzleList[type][index].band = -1
                    return false
                }

            },
            opentips(index) {  //点击区域后，显示图标
                if (!this.partList[index].type1 && !this.partList[index].type2) {
                    this.partList[index].type1 = true
                } else if (this.partList[index].type1 && !this.partList[index].type2) {
                    this.partList[index].type1 = false
                    this.partList[index].type2 = true
                } else if (!this.partList[index].type1 && this.partList[index].type2) {
                    this.partList[index].type2 = false
                }
            },
            load () {

            },
            finalgame() {   //游戏结束 进行游戏规则检查  这里可以取巧 因为每一个图的解是固定的 通过绑定位置可以解决所有check
                /*check1 全员连通 所有拼图连成一片
                //check2 同类相斥 同种拼图没有相互解除
                //check3 镂空覆盖 没有2*2的格子被拼图盖住*/
                for (let i = 0; i < this.checkList.length; i++) {
                    if (this.checkList[i] == -1) {
                        this.open4()
                        //console.log("game is not over1")
                        return
                    }
                }

                let answer = []
                for (let i = 0; i < this.N; i++) {
                    for (let j = 0; j < 4; j++) {
                        answer.push(this.finalList[i][j])
                    }
                }
                answer.sort(function (a, b) {
                    return a - b;
                })

                for (let i = 0; i < 4 * this.N; i++) {
                    if (this.realanswer[i] != answer[i]) {
                        this.open4()
                        //console.log("game is not over2")
                        return
                    }
                }
                this.open1()
                this.pid++
                //console.log("game over")


            },
            open1() {
                this.$notify({
                    title: '成功',
                    message: '拼图完成!',
                    type: 'success',
                    duration: 3000
                });
            },
            open2() {
                this.$notify({
                    title: '警告',
                    message: '这是一条警告的提示消息',
                    type: 'warning',
                    duration: 3000
                });
            },
            open3() {
                this.$notify.info({
                    title: '消息',
                    message: '这是一条消息的提示消息',
                    duration: 3000
                });
            },
            open4() {
                this.$notify.error({
                    title: '失败',
                    message: '不满足所有游戏规则',
                    duration: 3000
                });
            },
            //右键工具栏
            // TODO 对工具栏UI进行优化 UI还得继续优化 有点丑
            openMenu(e, type, index) {  //开启右键菜单
                if (this.puzzleList[type][index].id != this.iList[type] - 1) {
                    this.visible = true;
                } else {
                    this.visible = false;
                }

                this.rightClickType = type;
                this.rightClickIndex = index;
                this.target = e.target;

                var x = e.pageX;
                var y = e.pageY;

                this.top = y;
                this.left = x;
            },
            closeMenu() {
                this.visible = false;
            }

        }
    }
</script>

<style>
    .pintu_area {
        display: flex;
        flex-wrap: wrap;
        background-color: rgba(255, 255, 255, 0.9);
        -webkit-user-drag: none; /*图片不可被拖动*/
        position: absolute;
        top: 0;
        left: 0;
        bottom: 0;
        right: 0;
        margin: auto
    }

    .test {
        position: absolute;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .pintu_part {
        width: 56px;
        height: 56px;
        z-index: 100;
        border: 2px solid #0000001f;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .pick{
        -webkit-user-drag: none; /*图片不可被拖动*/
        position: absolute;
        bottom: 20%;
        right: 3%;
        width: 360px;
        height: 360px;
    }

    /*对话框*/
    div.el-dialog__header{
        padding: 0
    }
    div.el-dialog{
        --el-dialog-padding-primary: 0;
    }
    .play{
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .pingtu_cha {
        width: 30px;
        height: 30px;
        padding: 15px;
        -moz-user-select: none; /*图片不可被选中*/
        -webkit-user-select: none;
        -ms-user-select: none;
        -khtml-user-select: none;
        user-select: none;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .contextmenu {
        position: absolute;
        z-index: 3000;
    }

    .img1 {
        width: 120px;
        height: 200px;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .img2 {
        width: 50px;
        height: 50px;
        cursor: pointer;
        position: absolute;
        top: 0px;
        left: 50px;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .img3 {
        width: 50px;
        height: 50px;
        cursor: pointer;
        position: absolute;
        top: 75px;
        left: 112px;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .img4 {
        width: 50px;
        height: 50px;
        cursor: pointer;
        position: absolute;
        top: 150px;
        left: 50px;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    .part_img1 { /*长方形 1500 700*/
        width: 240px;
        height: 60px;
        background-size: 800px 600px;
        position: absolute;
        top: 20%;
        left: 8%;
        clip-path: polygon(2px 2px, 237px 2px, 237px 58px, 2px 58px);
        transform-origin: 120px 0;
    }

    .part_img2 { /*凸*/
        width: 180px;
        height: 120px;
        background-size: 800px 600px;
        position: absolute;
        top: 60%;
        left: 10%;
        clip-path: polygon(62px 2px, 118px 2px, 118px 62px, 178px 62px, 178px 118px, 2px 118px, 2px 62px, 62px 62px);
        transform-origin: 60px 60px;
    }

    .part_img3 { /*L*/
        width: 120px;
        height: 180px;
        background-size: 800px 600px;
        position: absolute;
        top: 35%;
        left: 2%;
        clip-path: polygon(2px 2px, 57px 2px, 57px 122px, 117px 122px, 117px 177px, 3px 177px);
        transform-origin: 60px 120px;
    }

    .part_img4 { /*Z*/
        width: 120px;
        height: 180px;
        background-size: 800px 600px;
        position: absolute;
        top: 35%;
        left: 22%;
        clip-path: polygon(62px 2px, 118px 2px, 118px 118px, 58px 118px, 58px 178px, 2px 178px, 2px 62px, 62px 62px);
        transform-origin: 60px 60px;
    }

    .backimage {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: -1;
    }

    .thumb,
    input {
        width: 200px;
        height: 150px;
        position: absolute;
        top: 0;
        right: 0;
        -moz-user-select: none;
        -webkit-user-select: none;
        -ms-user-select: none;
        -khtml-user-select: none;
        user-select: none;
        -webkit-user-drag: none; /*图片不可被拖动*/
    }

    input {
        opacity: 0;
    }

    @keyframes checked {
        50% {
            box-shadow: 0 0 20px #ffff00;
        }

        to {
            box-shadow: none;
        }
    }
    /*全局设置*/
    html, body, #app {
        height: 99%;
    }

    /*滚动条的宽度*/
    ::-webkit-scrollbar {
        width:9px;
        height:9px;
    }
    /*外层轨道。可以用display:none让其不显示，也可以添加背景图片，颜色改变显示效果*/
    ::-webkit-scrollbar-track {
        width: 6px;
        background-color:#0d1b20;
        -webkit-border-radius: 2em;
        -moz-border-radius: 2em;
        border-radius:2em;
    }
    /*滚动条的设置*/
    ::-webkit-scrollbar-thumb {
        background-color:#606d71;
        background-clip:padding-box;
        min-height:28px;
        -webkit-border-radius: 2em;
        -moz-border-radius: 2em;
        border-radius:2em;
    }
    /*滚动条移上去的背景*/
    ::-webkit-scrollbar-thumb:hover {
        background-color:#fff;
    }
</style>
