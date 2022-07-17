import { mount } from '@vue/test-utils'
import {createVuetify} from "vuetify"
import NavBar from '../../NavBar.vue'
import router from "@/router";

describe('ActionButton.vue', () => {
    let wrapper;
    beforeEach(() => {
        const vuetify = createVuetify()
        jest.clearAllMocks()
        wrapper = mount(NavBar, {
            global: {
                plugins: [
                    vuetify,
                    router
                ],
            },
        })
    })

    describe("Component", () => {
        it("components are Vue instances", () => {
            expect(wrapper.vm).toBeTruthy();
        })
    })
})