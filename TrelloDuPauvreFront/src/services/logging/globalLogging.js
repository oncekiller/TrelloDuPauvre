
import axios from "axios"
import { subHours } from 'date-fns';
const DB_NAME = "trelloDuPauvreLogging"
const REQUEST_LOGS_STORE_NAME = "requestLogs"
const CONSOLE_LOGS_STORE_NAME = "consoleLogs"
const SETTINGS_STORE_NAME = "settings"
const LOG_LEVEL = 2 //0 VERBOSE / 1 INFO / 2 WARN / 3 DEBUG
const MAX_BODY_LENGTH = 1000 //Max number of caracters for a body (if supperior we don't save the body )
const NB_HOURS_STORAGE = 1 //Nb of hours of storage in database
const NB_HOURS_DELAY = 2 //Min number of hours between two database cleaning
let db

//Convert body object datas to string
function convertBodyToString(body) {
    if (body) {
        const bodyText = JSON.stringify(body)
        return bodyText.length > MAX_BODY_LENGTH ? "" : bodyText
    } else {
        return ""
    }
}
//Format axios request data to database log format
function createRequestLog(type, datas) {
    const dateTime = new Date()
    if (type === "request") {
        return {
            type: "REQUEST",
            url: datas.url,
            method: datas.method,
            params: datas.params,
            body: convertBodyToString(datas.data),
            dateTime: dateTime.toISOString(),
        }
    } else {
        let requestLogResponse = {
            type: "RESPONSE",
            url: datas.config?.url,
            method: datas.config?.method,
            status: datas.status,
            body: convertBodyToString(datas.data),
            dateTime: dateTime.toISOString(),
        }
        if (LOG_LEVEL < 2) {
            //Log level INFO or Verbose
            requestLogResponse = {
                ...requestLogResponse,
                requestBody: datas.config?.data
            }
        }
        return requestLogResponse
    }
}
const getLastCleanDateTime = () => {
    return new Promise((resolve, reject) => {
        const trans = db.transaction([SETTINGS_STORE_NAME], 'readonly')
        const store = trans.objectStore(SETTINGS_STORE_NAME)
        const request = store.get("lastCleanDateTime")
        trans.oncomplete = async () => {
            resolve(request.result?.value)
        }

        trans.onerror = e => {
            reject('Error reading db : ' + e.target?.error);
        };
    })
}
//Download file logs
function downloadFileLogs(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}
//Add log to indexDB table
const addLogToDb = async (storeName, log) => {
    return new Promise((resolve, reject) => {
        const trans = db.transaction([storeName], 'readwrite')
        const store = trans.objectStore(storeName)
        store.add(log)
        trans.oncomplete = async (e) => {
            resolve(e.target.result)
        }

        trans.onerror = e => {
            reject('Error adding into db : ' + e.target?.error);
        };
    })
}
const deleteAllPassedLogsFromTable = async (storeName, limitDateTime) => {
    return new Promise((resolve, reject) => {

        let trans = db.transaction([storeName], 'readwrite');
        let store = trans.objectStore(storeName);
        let index = store.index("dateTime")
        let logs = [];

        index.openCursor().onsuccess = e => {
            let cursor = e.target.result;
            if (cursor) {
                var key = cursor.key
                while (new Date(key) > limitDateTime) {
                    cursor.continue();
                    return
                }
                cursor.delete()
                cursor.continue();
            }
        };

        trans.oncomplete = () => {
            resolve(logs);
        };

        trans.onerror = e => {
            reject('Error reading db : ' + e.target?.error);
        };

    });
}
//Get all logs from indexDB table
const getAllLogsFromTable = async (storeName) => {
    return new Promise((resolve, reject) => {

        let trans = db.transaction([storeName], 'readonly');
        let store = trans.objectStore(storeName);
        let logs = [];

        store.openCursor().onsuccess = e => {
            let cursor = e.target.result;
            if (cursor) {
                logs.push(cursor.value)
                cursor.continue();
            }
        };

        trans.oncomplete = () => {
            resolve(logs);
        };

        trans.onerror = e => {
            reject('Error reading db : ' + e.target?.error);
        };

    });
}
//Instantiate IndexDb database
const getDb = async () => {
    return new Promise((resolve, reject) => {
        //Retrieve index db
        const request = window.indexedDB.open(DB_NAME);

        request.onerror = e => {
            reject('Error opening db : ' + e.target?.error);
        };

        request.onsuccess = async e => {
            const db = e.target.result
            //Detect if all tables exist
            const requestLogsTableExist = db?.objectStoreNames?.contains(REQUEST_LOGS_STORE_NAME)
            const consoleLogsTableExist = db?.objectStoreNames?.contains(CONSOLE_LOGS_STORE_NAME)
            const settingsTableExist = db?.objectStoreNames?.contains(SETTINGS_STORE_NAME)
            if (requestLogsTableExist && consoleLogsTableExist && settingsTableExist) {
                resolve(db);
            } else {
                //If one table missing => recreate new version of db with all the tables
                let newVersionRequest = window.indexedDB.open(DB_NAME, db.version + 1);
                newVersionRequest.onsuccess = e => {
                    resolve(e.target.result)
                }
                newVersionRequest.onupgradeneeded = e => upgradeDb(e)
            }
        };

        request.onupgradeneeded = e => upgradeDb(e)
    });
}
//Upgrade indexDb database tables
function upgradeDb(event) {
    console.log('onupgradeneeded');
    const db = event.target.result;
    if (!db?.objectStoreNames?.contains(REQUEST_LOGS_STORE_NAME)) {
        const requestLogsStore = db.createObjectStore(REQUEST_LOGS_STORE_NAME, { autoIncrement: true, keyPath: 'id' });
        requestLogsStore.createIndex("dateTime", "dateTime", { unique: false });
    }
    if (!db?.objectStoreNames?.contains(CONSOLE_LOGS_STORE_NAME)) {
        const consoleLogsStore = db.createObjectStore(CONSOLE_LOGS_STORE_NAME, { autoIncrement: true, keyPath: 'id' });
        consoleLogsStore.createIndex("dateTime", "dateTime", { unique: false });
    }
    if (!db?.objectStoreNames?.contains(SETTINGS_STORE_NAME)) {
        db.createObjectStore(SETTINGS_STORE_NAME, { keyPath: 'name' });
    }
}
//Control if database needs to be cleaned
const controlCleanDb = async () => {
    const lastCleanDateTime = new Date(await getLastCleanDateTime())
    const actualDateTime = new Date()
    const maxCleanDateTimeRenew = subHours(actualDateTime, NB_HOURS_DELAY)
    if ((lastCleanDateTime - maxCleanDateTimeRenew) < 0) {
        //last cleaning db > NB_HOURS_DELAY
        const limitDateTime = subHours(actualDateTime, NB_HOURS_STORAGE) // limit datetime for cleaning rows in db tables
        await cleanDb(limitDateTime)
        await updateLastCleanDateTime ({ name: "lastCleanDateTime", value: actualDateTime.toISOString() })
    }
}
//Clean all passed logs from tables of the db
const cleanDb = async (limitDateTime) => {
    console.log("start cleaning db")
    await deleteAllPassedLogsFromTable(REQUEST_LOGS_STORE_NAME, limitDateTime)
    await deleteAllPassedLogsFromTable(CONSOLE_LOGS_STORE_NAME, limitDateTime)
    console.log("end cleaning db")
}
const updateLastCleanDateTime = async (lastCleanDateTimeObj) => {
    return new Promise((resolve, reject) => {
        const trans = db.transaction([SETTINGS_STORE_NAME], 'readwrite')
        const store = trans.objectStore(SETTINGS_STORE_NAME)
        const request = store.openCursor("lastCleanDateTime");
        request.onsuccess = async (e) => {
            var cursor = e.target.result;
            if (cursor) { // key already exist
                cursor.update(lastCleanDateTimeObj);
            } else { // key not exist
                store.add(lastCleanDateTimeObj)
            }
        };
        trans.oncomplete = async (e) => {
            resolve(e.target.result)
        }

        trans.onerror = e => {
            reject('Error adding into db : ' + e.target?.error);
        };
    })
}
export default {
    controlCleanDb,
    async downloadRequestLogs() {
        const dateTime = new Date()
        const logs = await getAllLogsFromTable(REQUEST_LOGS_STORE_NAME)
        console.log(logs)
        let downloadText = ""
        logs.forEach(log => {
            if (log.type === "REQUEST") {
                downloadText += `${log.dateTime || ""} ${log.type || ""} ${log.method || ""} ${log.url || ""} ${JSON.stringify(log.params) || ""} ${log.body || ""}\r\n`
            } else if (log.type === "RESPONSE") {
                downloadText += `${log.dateTime || ""} ${log.type || ""} ${log.method || ""} ${log.url || ""} ${log.status || ""} ${log.body || ""}\r\n`
            }
        });
        downloadFileLogs(`RequestLogs_${dateTime.toISOString()}.txt`, downloadText)
    },
    async initIndexDBLogging() {
        //Instanciate indexDb
        db = await getDb()
        const lastCleanDateTime = await getLastCleanDateTime()
        if (!lastCleanDateTime) {
            await updateLastCleanDateTime ({ name: "lastCleanDateTime", value: new Date().toISOString() })
        }
        await controlCleanDb()
        // Add a request interceptor
        axios.interceptors.request.use(async function (request) {
            //Intercept all request
            if (LOG_LEVEL > 1) {
                //If log level WARN or more
                const requestLog = createRequestLog("request", request)
                addLogToDb(REQUEST_LOGS_STORE_NAME, requestLog).catch(() => { })
            }
            return request;
        });

        // Add a response interceptor
        axios.interceptors.response.use(function (response) {
            //Intercept all response success (status == 2XX)
            if (LOG_LEVEL > 1) {
                //If log level WARN or more
                const requestLog = createRequestLog("response", response)
                addLogToDb(REQUEST_LOGS_STORE_NAME, requestLog).catch(() => { })
            }
            return response;
        }, function (error) {
            //Intercept all response error (status != 2XX)
            if (LOG_LEVEL > 0) {
                //If log level INFO or more
                const response = error.response
                const requestLog = createRequestLog("response", response)
                addLogToDb(REQUEST_LOGS_STORE_NAME, requestLog).catch(() => { })
            }
            throw error
        });
    }
}