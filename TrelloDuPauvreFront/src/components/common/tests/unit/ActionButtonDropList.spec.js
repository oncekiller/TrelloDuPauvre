import { mount } from '@vue/test-utils'
import {createVuetify} from "vuetify"
import ActionButtonDropList from '../../ActionButtonDropList.vue'

describe('ActionButton.vue', () => {
    let wrapper;
    beforeEach(() => {
        const vuetify = createVuetify()
        jest.clearAllMocks()
        wrapper = mount(ActionButtonDropList, {
            props: {
                elementsList: ["element1", "element2"],
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
        it("click trigger display dropList", async () => {
            expect(wrapper.find(".actionButtonDropListMain").exists()).toBe(false)
            await wrapper.find(".actionButtonDropList").trigger("click")
            expect(wrapper.find(".actionButtonDropListMain").exists()).toBe(true)
        })
        //icon close
        it("isCloseDisplay true icon close display", async () => {
            const index = 1
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index
            })
            expect(wrapper.find(".mdi-close").exists()).toBe(true)
        })
        it("isCloseDisplay false icon close not display", () => {
            expect(wrapper.find(".mdi-close").exists()).toBe(false)
        })
        it("isCloseDisplay true click icon handleClose", async () => {
            const index = 1
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index
            })
            await wrapper.find(".mdi-close").trigger("click")
            expect(wrapper.vm.selectedElement).toEqual(preSelectionField);
            expect(wrapper.vm.selectedElementIndexData).toEqual(0);
            expect(wrapper.vm.open).toEqual(false);
            expect(wrapper.find(".actionButtonDropListMain").exists()).toBe(false)
        })
        //Class selected
        it("select element index 1 class selected", async () => {
            const index = 1
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index,
                selectedElement: wrapper.vm.elementsList?.[index],
                open: true
            })
            expect(wrapper.find(`.actionButtonDropListMain > .v-col > .v-row:nth-child(${index + 1})`).classes()).toContain("selected")
        })
        it("preSelectionField element index 0 class no selected", async () => {
            const index = 0
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index,
                selectedElement: wrapper.vm.elementsList?.[index],
                open: true
            })

            expect(wrapper.find(`.actionButtonDropListMain > .v-col > .v-row:nth-child(${index + 1})`).classes()).not.toContain("selected")
        })
    })

    describe("Data", () => {
        it("selectedElement with no preSelectionField", async () => {
            const vuetify = createVuetify()
            const index = 1
            const localWrapper = mount(ActionButtonDropList, {
                props: {
                    elementsList: ["element1", "element2"],
                    selectedElementIndex: index,
                },
                global: {
                    plugins: [vuetify],
                },
            })
            expect(localWrapper.vm.selectedElement).toEqual(localWrapper.vm.elementsList?.[index]);
        })
        it("selectedElement with preSelectionField index = -1", async () => {
            const vuetify = createVuetify()
            const index = -1
            const preSelectionField = "preSelectionField"
            const localWrapper = mount(ActionButtonDropList, {
                props: {
                    elementsList: ["element1", "element2"],
                    selectedElementIndex: index,
                    preSelectionField: preSelectionField
                },
                global: {
                    plugins: [vuetify],
                },
            })
            expect(localWrapper.vm.selectedElement).toEqual(preSelectionField);
        })
        it("selectedElement with preSelectionField index != -1", async () => {
            const vuetify = createVuetify()
            const index = 1
            const preSelectionField = "preSelectionField"
            const localWrapper = mount(ActionButtonDropList, {
                props: {
                    elementsList: ["element1", "element2"],
                    selectedElementIndex: index,
                    preSelectionField: preSelectionField
                },
                global: {
                    plugins: [vuetify],
                },
            })
            expect(localWrapper.vm.selectedElement).toEqual(localWrapper.vm.elementsList?.[index]);
        })
    })
    describe("Method", () => {
        //click
        it("click open ", async () => {
            await wrapper.vm.click()
            expect(wrapper.vm.open).toEqual(true)
        })
        //handleSelection
        it("handleSelection update data", async () => {
            const index = 1
            await wrapper.vm.handleSelection(index)
            expect(wrapper.vm.selectedElementIndexData).toEqual(index)
            expect(wrapper.vm.selectedElement).toEqual(wrapper.vm.elementsList?.[index])
        })
        it("handleSelection emit handleSelectionAction with index", async () => {
            const index = 1
            await wrapper.vm.handleSelection(index)
            expect(wrapper.emitted().handleSelectionAction).toHaveLength(1)
            expect(wrapper.emitted().handleSelectionAction).toEqual([[index]])
        })
    })
    describe("Computed", () => {
        //isCloseDisplay
        it("isCloseDisplay false no preSelectionField", async () => {
            expect(wrapper.vm.isCloseDisplay).toEqual(false)
        })
        it("isCloseDisplay false preSelectionField index = 0", async () => {
            const index = 0
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index
            })
            expect(wrapper.vm.isCloseDisplay).toEqual(false)
        })
        it("isCloseDisplay true preSelectionField index != 0", async () => {
            const index = 1
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index
            })
            expect(wrapper.vm.isCloseDisplay).toEqual(true)
        })
        //isChevronDisplay
        it("isChevronDisplay true no preSelectionField", async () => {
            expect(wrapper.vm.isChevronDisplay).toEqual(true)
        })
        it("isChevronDisplay true preSelectionField index = 0", async () => {
            const index = 0
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index
            })
            expect(wrapper.vm.isChevronDisplay).toEqual(true)
        })
        it("isChevronDisplay false preSelectionField index != 0", async () => {
            const index = 1
            const preSelectionField = "preSelectionField"
            await wrapper.setProps({
                preSelectionField: preSelectionField,
            })
            await wrapper.setData({
                selectedElementIndexData: index
            })
            expect(wrapper.vm.isChevronDisplay).toEqual(false)
        })
    })
})