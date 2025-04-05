#version 300 es
precision highp float;

in vec2 vTextureCoord;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform float uTime;
uniform vec2 uResolution;
uniform float uGlitchIntensity;
uniform float uDistortionAmount;
uniform vec3 uGlitchColor;

// Noise functions
float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = rand(i);
    float b = rand(i + vec2(1.0, 0.0));
    float c = rand(i + vec2(0.0, 1.0));
    float d = rand(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// Glitch block function
vec2 glitchBlock(vec2 uv) {
    float block = floor(uv.y * 10.0);
    float time = uTime * 2.0;
    float noise = rand(vec2(block, floor(time * 20.0)));
    
    if (noise > 0.95) {
        uv.x += (noise - 0.95) * 1.0;
    }
    
    return uv;
}

// RGB split effect
vec4 rgbSplit(sampler2D tex, vec2 uv, float amount) {
    vec2 offset = amount * vec2(0.01, 0.0);
    
    vec4 r = texture(tex, uv + offset);
    vec4 g = texture(tex, uv);
    vec4 b = texture(tex, uv - offset);
    
    return vec4(r.r, g.g, b.b, 1.0);
}

// Scanline effect
float scanline(vec2 uv) {
    return sin(uv.y * uResolution.y * 0.5 - uTime * 5.0) * 0.5 + 0.5;
}

// Wave distortion
vec2 waveDistort(vec2 uv) {
    vec2 distort;
    distort.x = sin(uv.y * 20.0 + uTime) * 0.003 * uDistortionAmount;
    distort.y = cos(uv.x * 20.0 + uTime) * 0.003 * uDistortionAmount;
    return uv + distort;
}

void main() {
    vec2 uv = vTextureCoord;
    
    // Apply wave distortion
    uv = waveDistort(uv);
    
    // Apply glitch blocks
    if (uGlitchIntensity > 0.0) {
        uv = glitchBlock(uv);
    }
    
    // RGB split with variable intensity
    vec4 color = rgbSplit(uTexture, uv, uGlitchIntensity * 0.1);
    
    // Add noise
    float noiseValue = noise(uv * 500.0 + uTime);
    color.rgb += noiseValue * uGlitchIntensity * 0.1;
    
    // Add scanlines
    float scan = scanline(uv);
    color.rgb *= 0.9 + scan * 0.1;
    
    // Add chromatic aberration at edges
    float vignetteAmount = 1.0 - distance(uv, vec2(0.5));
    color.rgb *= vignetteAmount;
    
    // Color tinting based on glitch intensity
    color.rgb = mix(color.rgb, uGlitchColor, uGlitchIntensity * 0.2);
    
    // Add digital glitch effects
    if (rand(vec2(uTime, uv.y)) < uGlitchIntensity * 0.1) {
        color.rgb = vec3(1.0) - color.rgb; // Color inversion
    }
    
    // Add vertical distortion lines
    float lineNoise = step(0.98, rand(vec2(uTime, floor(uv.y * 20.0))));
    color.rgb = mix(color.rgb, vec3(1.0), lineNoise * uGlitchIntensity);
    
    // Final color output
    FragColor = color;
}
