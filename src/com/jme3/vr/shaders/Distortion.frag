uniform sampler2D m_Texture;
varying vec2 texCoord;

uniform float   m_K1_Red;
uniform float   m_K1_Green;
uniform float   m_K1_Blue;
uniform vec2    m_Center;

vec2 Distort(vec2 p, float k1){
    /// @todo would pow improve performance here? (by using SFU if available?)
        float r2 = p.x * p.x + p.y * p.y;

        float newRadius = (1.0 + k1*r2);
        p.x = p.x * newRadius;
        p.y = p.y * newRadius;

        return p;
}

void main(){
    vec2 uv_red, uv_green, uv_blue;
    vec4 color_red, color_green, color_blue;
    vec2 sectorOrigin;
    vec4 color;

    sectorOrigin = m_Center.xy;

    uv_red = Distort(texCoord-sectorOrigin, m_K1_Red) + sectorOrigin;
    uv_green = Distort(texCoord-sectorOrigin, m_K1_Green) + sectorOrigin;
    uv_blue = Distort(texCoord-sectorOrigin, m_K1_Blue) + sectorOrigin;

    color_red = texture2D(m_Texture, uv_red);
    color_green = texture2D(m_Texture, uv_green);
    color_blue = texture2D(m_Texture, uv_blue);

    if( ((uv_red.x > 0.0) && (uv_red.x < 1.0) && (uv_red.y > 0.0) && (uv_red.y < 1.0))){
        color = vec4(color_red.x, color_green.y, color_blue.z, 1.0);
    } else {
        color = vec4(0.0,0.0,0.0,1.0);
    }
    gl_FragColor = color;//texture2D(m_Texture, texCoord);
}
