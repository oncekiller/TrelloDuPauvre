import { mount } from '@vue/test-utils'
import {createVuetify} from "vuetify"
import CheckListItem from '../../CheckListItem.vue'

describe('CheckListItem.vue', () => {
    let wrapper;
    beforeEach(() => {
        const vuetify = createVuetify()
        jest.clearAllMocks()
        wrapper = mount(CheckListItem, {
            props: {
                index: 1,
                checkListItemLabel: "checkListItemLabel"
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