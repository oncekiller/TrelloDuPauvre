import { mount } from '@vue/test-utils'
import {createVuetify} from "vuetify"
import EditField from '../../EditField.vue'

describe('ActionButton.vue', () => {
    let wrapper;
    beforeEach(() => {
        const vuetify = createVuetify()
        jest.clearAllMocks()
        wrapper = mount(EditField, {
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

    describe("Method", () => {
        //handleValidation
        it("handleValidation emit handleValidationAction", async () => {
            const newFieldValue = "newFieldValue"
            wrapper.vm.newFieldValue = newFieldValue
            await wrapper.vm.handleValidation()
            expect(wrapper.emitted().handleValidationAction).toHaveLength(1)
            expect(wrapper.emitted().handleValidationAction).toEqual([[newFieldValue]])
        })
        //handleClose
        it("handleClose emit handleCloseAction", async () => {
            await wrapper.vm.handleClose()
            expect(wrapper.emitted().handleCloseAction).toHaveLength(1)
        })
    })
})