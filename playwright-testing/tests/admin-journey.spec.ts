import { test, expect } from "@playwright/test";
import { loginAs } from "./auth-helper";

test.describe("System Administrator Enterprise Console Journey", () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, "admin");
  });

  test("Admin Overview: inspects system health and global platform metrics", async ({ page }) => {
    await expect(page.locator("h1")).toContainText("System Administration Console");
    await expect(page.locator("text=Platform Status")).toBeVisible();
    await expect(page.locator("text=Total Users")).toBeVisible();
    await expect(page.locator("text=Policies Bound")).toBeVisible();
  });

  test("Audits User Directory, Global Policies, and Care Trust Ledger tabs", async ({ page }) => {
    // User Directory tab in the dashboard tab bar
    await page.locator("button:has-text(\"User Directory\")").click();
    await expect(page.locator("h3:has-text(\"Authorized User Accounts\")")).toBeVisible();

    // Global Policies tab
    await page.locator("button:has-text(\"Global Policies\")").click();
    await expect(page.locator("h3:has-text(\"Enterprise Policy Registry\")")).toBeVisible();

    // Care Trust Ledger tab
    await page.locator("button:has-text(\"Care Trust Ledger\")").click();
    await expect(page.locator("h3:has-text(\"Pet Care Trust Master Ledger\")")).toBeVisible();
  });

  test("Claims & Manual Review: audits claims queue and triggers manual review actions", async ({ page }) => {
    // Navigate to Claims & Manual Review tab
    await page.locator("button:has-text(\"Claims & Manual Review\")").click();
    await expect(page.locator("h3:has-text(\"Pet Continuity Claims Adjudication\")")).toBeVisible();

    // Check filter buttons
    await page.locator("button:has-text(\"Manual Review Required\")").click();
    await page.locator("button:has-text(\"All Claims\")").click();
    await expect(page.locator("table")).toBeVisible();
  });
});
