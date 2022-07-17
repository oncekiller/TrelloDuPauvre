import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
export default {
    namespaced: true,
    state: {
        
    },
    getters: {
        
    },
    mutations: {
        
    },
    actions: {
        async createImage(none, file) {
            const formData = new FormData();
            formData.append("file", file);
            return await trelloDuPauvreApiService.post("/images", formData)
        }, 
        async deleteImageById(none, imageId ){
            await trelloDuPauvreApiService.delete("/images/" + imageId)
        },
        async getImageById(none, imageId){
            const image = await trelloDuPauvreApiService.get("/images/" + imageId)
            return image
        },
    }
}