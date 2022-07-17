import { mount } from '@vue/test-utils'
import {createVuetify} from "vuetify"
import ActionButton from '../../ActionButton.vue'

describe('ActionButton.vue', () => {
    let wrapper;
    beforeEach(() => {
        const vuetify = createVuetify()
        jest.clearAllMocks()
        wrapper = mount(ActionButton, {
            props: {
                action: async () => {
                    await wrapper.setProps({
                        text: "i"
                    })
                },
                text: ""
            },
            global: {
                plugins: [vuetify],
            },
        })
    })

    describe("Component", () => {
        it("components are Vue instances", () => {
            expect(wrapper.vm).toBeTruthy();
        })
        it("button contain text props", async () => {
            const text = "text"
            await wrapper.setProps({
                text: text
            })
            expect(wrapper.find(".actionButton").text()).toEqual(text);
        })
        it("button have class variantYellow", async () => {
            const type = "variantYellow"
            expect(wrapper.find(".actionButton").classes()).toContain(type);
        })
    })

    describe("Method", () => {
        it("click button do action", async () => {
            await wrapper.find(".actionButton").trigger('click')
            expect(wrapper.props("text")).toEqual("i")
        })
    })
})