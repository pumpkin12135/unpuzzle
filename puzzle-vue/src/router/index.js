import {createRouter, createWebHashHistory} from 'vue-router'

import Puzzle from '../views/Puzzle.vue'
import Login from '../views/login.vue'

const routes = [
    {
        path: '/',
        name: 'Index',
        redirect: {name: "Login"}
    },
    {
        path: '/puzzle',
        name: 'Puzzle',
        component: Puzzle
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    }
]

const router = createRouter({
    mode: 'hash',
    history: createWebHashHistory(),
    routes
})

export default router