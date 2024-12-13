/**
 * 转换图片 URL 为代理 URL
 * @param {string} url 原始图片 URL
 * @returns {string} 代理后的 URL
 */
export function convertToProxyUrl(url) {
  if (!url) return url
  
  try {
    const proxyEnabled = localStorage.getItem('imageProxyEnabled') === 'true'
    if (!proxyEnabled) return url

    // 使用当前访问地址作为代理地址
    const serverUrl = window.location.origin
    if (!serverUrl) return url

    const newUrl = new URL(serverUrl)
    const currentUrl = new URL(url)
    
    // 如果域名已经相同，则不需要代理
    if (currentUrl.host === newUrl.host) return url

    // 只保留路径部分
    const pathAndQuery = currentUrl.pathname + currentUrl.search + currentUrl.hash
    
    // 构建新的 URL，移除可能的重复斜杠
    const baseUrl = serverUrl.endsWith('/') ? serverUrl.slice(0, -1) : serverUrl
    const path = pathAndQuery.startsWith('/') ? pathAndQuery : '/' + pathAndQuery
    
    return baseUrl + path
  } catch (error) {
    console.warn('Failed to proxy image URL:', error)
    return url
  }
}

/**
 * 转换多个图片 URL 为代理 URL
 * @param {string[]} urls 原始图片 URL 数组
 * @returns {string[]} 代理后的 URL 数组
 */
export function convertUrlsToProxy(urls) {
  if (!Array.isArray(urls)) return []
  return urls.map(url => convertToProxyUrl(url))
} 