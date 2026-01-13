from playwright.sync_api import sync_playwright, expect
import json

def verify_chat_ui():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()

        # Mock Local Storage for Authentication
        user_info = {
            "userId": "test-user-123",
            "username": "TestUser",
            "role": "USER"
        }

        # Navigate to app (it might redirect to login first)
        page.goto("http://localhost:4200/login")

        # Inject local storage
        page.evaluate(f"localStorage.setItem('USER_INFO', '{json.dumps(user_info)}');")

        # Navigate to dashboard/chat
        page.goto("http://localhost:4200/dashboard/chat")

        # Mock the chat endpoint
        def handle_chat(route):
            route.fulfill(
                status=200,
                body="**Sure!** Here is a response with some markdown.\n\n* Item 1\n* Item 2\n\nHope this helps!",
                headers={"Content-Type": "text/plain"}
            )

        page.route("**/trainer/chat", handle_chat)

        # Verify page elements
        expect(page.get_by_role("heading", name="Agentic Chat")).to_be_visible()
        expect(page.get_by_placeholder("E.g., How can I improve my deadlift form?")).to_be_visible()

        # Type a prompt
        page.get_by_placeholder("E.g., How can I improve my deadlift form?").fill("Hello trainer!")

        # Click Send
        page.get_by_role("button", name="Send").click()

        # Wait for response
        expect(page.get_by_text("Trainer Response")).to_be_visible()
        expect(page.get_by_text("Sure!")).to_be_visible() # Check for bold text rendered content

        # Take screenshot
        page.screenshot(path="verification/agentic_chat.png")

        browser.close()

if __name__ == "__main__":
    verify_chat_ui()
