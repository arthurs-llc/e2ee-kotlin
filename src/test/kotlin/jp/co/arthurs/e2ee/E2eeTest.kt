package jp.co.arthurs.e2ee

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [TestConfig::class])
@AutoConfigureMockMvc
internal class E2eeTest(@Autowired val mockMvc: MockMvc, @Autowired val objectMapper: ObjectMapper) {

    @Test
    fun test() {
        // retrieve public key
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/e2ee/public-key"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .response
        val keyIdAndPublicKey = objectMapper.readValue(response.contentAsString, KeyIdAndPublicKey::class.java)

        // encrypt password
        val plainPassword = "hello"
        val encryptedPassword = RsaUtils.encrypt(Base64.getUrlDecoder().decode(keyIdAndPublicKey.publicKey), plainPassword.toByteArray())
        val base64EncryptedPassword = Base64.getUrlEncoder().encodeToString(encryptedPassword)

        // login with encrypted password
        val credential = E2eeCredential("hello@example.com", base64EncryptedPassword, keyIdAndPublicKey.keyId)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/e2ee/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credential)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }
}
