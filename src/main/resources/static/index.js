window.addEventListener('DOMContentLoaded', (event) => {
    document.querySelector('button').addEventListener('click', async function(e) {
        const keyIdAndPublicKey = await (await fetch("http://localhost:8080/e2ee/public-key")).json()
        const base64PublicKey = keyIdAndPublicKey.publicKey.replace(/-/gi, '+').replace(/_/gi, '/')
        const importedKey = await importPublicKey(base64PublicKey)

        const encryptedPassword = await encryptData(importedKey, document.querySelector('#password').value)
        const encodedPassword = btoa(ab2str(encryptedPassword)).replace(/\+/gi, '-').replace(/\//gi, '_')
        await fetch("http://localhost:8080/e2ee/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                email: document.querySelector('#email').value,
                password: encodedPassword,
                keyId: keyIdAndPublicKey.keyId
            })
        })
    })
})

async function importPublicKey(publicKey) {
    return await window.crypto.subtle.importKey('spki', str2ab(atob(publicKey)), {name: 'RSA-OAEP', hash: 'SHA-256'}, true, ['encrypt'])
}

function str2ab(str) {
    const buf = new ArrayBuffer(str.length)
    const bufView = new Uint8Array(buf)
    bufView.forEach((num, index) => {
        bufView[index] = str.charCodeAt(index)
    })
    return buf
}

function ab2str(ab) {
    return String.fromCharCode.apply(null, Array.from(new Uint8Array(ab)))
}

async function encryptData(cryptoKey, text) {
    return await window.crypto.subtle.encrypt({name: 'RSA-OAEP'}, cryptoKey, str2ab(text))
}