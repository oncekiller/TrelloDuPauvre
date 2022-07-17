import { mount } from '@vue/test-utils'
import {createVuetify} from "vuetify"
import StoryPanel from '../../StoryPanel.vue'

describe('StoryPanel.vue', () => {
    let wrapper;
    beforeEach(() => {
        const vuetify = createVuetify()
        jest.clearAllMocks()
        wrapper = mount(StoryPanel, {
            props: {
                storyId: 1,
                tickets: [
                    {
                        ticketId: 1,
                        name: "ticket1"
                    },
                    {
                        ticketId: 2,
                        name: "ticket2"
                    }
                ]
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
    })
})