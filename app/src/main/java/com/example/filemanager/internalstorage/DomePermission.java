package com.example.filemanager.internalstorage;

public class DomePermission {

    /*
    private void requestAppropriateStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ media-specific permissions
            checkStoragePermissionForAndroid13();
        } else if (SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (MANAGE_EXTERNAL_STORAGE)
            checkManageAllFilesPermission();
        } else {
            // Android 10 and below
            checkLegacyStoragePermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkStoragePermissionForAndroid13() {
        boolean hasImagePermission = checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        boolean hasVideoPermission = checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!hasImagePermission || !hasVideoPermission || !hasAudioPermission) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            }, REQUEST_MEDIA_PERMISSIONS);
        } else {
            Toast.makeText(this, "Media permissions granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkManageAllFilesPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_ACCESS);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Permission screen not found on this device", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Optional: fallback for Android 10 and below
            checkLegacyStoragePermission();
        }

    }

    private void checkLegacyStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE
                    },
                    REQUEST_LEGACY_STORAGE_PERMISSIONS);
        } else {
            Toast.makeText(this, "Storage permissions granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_MEDIA_PERMISSIONS || requestCode == REQUEST_LEGACY_STORAGE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MANAGE_ALL_FILES_ACCESS) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "All files access granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "All files access denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
     */

}
