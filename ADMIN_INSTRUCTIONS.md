# Admin User Instructions

This document explains how to use the admin features in the Entry Tablet App.

## App Purpose

Entry Tablet App is used to:
- Sign visitors in
- Sign visitors out
- View and manage visitor logs
- Manage the list of people that visitors can select when signing in

## Accessing Admin

1. From the home screen, tap **Admin** (bottom-right).
2. Enter the admin password.
3. Tap **Enter**.

> Current default admin password: `admin`

If the password is incorrect, the app shows an **Incorrect password** message.

## Admin Screen Overview

The Admin page includes:
- **Manage people to see** section
- Visitor **log table** (name, person to see, sign-in time, sign-out time)
- **Clear All** button
- **Export CSV** button

The page supports scrolling to access all content on smaller screens.

## Managing “People to See”

Use this section to maintain who visitors can choose when signing in.

### Add a person

1. In **Person to see name**, type the name.
2. Tap **Add**.

Expected behavior:
- If valid and new: person is added.
- If previously removed: person may be restored.
- If duplicate: app shows a duplicate message.
- If empty: app asks for a name.

### Remove a person

1. Find the person in the list.
2. Tap **Remove** next to their name.

The person will no longer appear as a selectable option on visitor sign-in.

## Viewing the Visitor Log

The log table displays visitor entries with:
- Name
- Person to See
- Sign In Time
- Sign Out Time

If no records exist, the app displays **No records found.**

## Exporting Log Data (CSV)

1. Tap **Export CSV**.
2. The app generates a CSV export of visitor records.
3. Save/share the file as prompted by Android.

Use exported CSV files for reporting or archival.

## Clearing All Records

1. Tap **Clear All**.
2. Confirm when prompted.

⚠️ This permanently deletes all visitor records and cannot be undone.

## Installing on a Tablet (Admin Quick Notes)

### Build debug APK

In Android Studio:
1. Open the project.
2. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
3. Locate output:

`app/build/outputs/apk/debug/app-debug.apk`

### Install to connected device with ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

If updating an existing install, the `-r` flag replaces it.

## Basic Troubleshooting

### Admin page doesn’t show full content

- Scroll the Admin page (it is vertically scrollable).
- Verify app is updated to latest build.

### Cannot access Admin

- Confirm correct password was entered.
- Check for keyboard auto-capitalization/spaces.

### CSV export not found

- Check Android Downloads / Files app.
- Re-run export and watch for save/share prompt.

### APK won’t install

- Enable install permission for your file manager/browser (if sideloading).
- Make sure device has enough storage.
- Use `adb install -r` for upgrades.

## Operational Best Practices

- Periodically export CSV for backup.
- Limit admin password access to authorized staff.
- After staff changes, review and update the “People to See” list.
